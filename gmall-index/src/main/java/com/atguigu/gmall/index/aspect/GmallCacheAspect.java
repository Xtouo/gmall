package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RBloomFilter bloomFilter;

    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取目标方法
        Method method = signature.getMethod();
        // 获取目标方法上的注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        // 获取注解中prefix参数
        String prefix = gmallCache.prefix();
        // 获取目标方法参数，并以逗号分割成字符串
        String ages = StringUtils.join(joinPoint.getArgs(), ",");
        // 组装成缓存的Key
        String key = prefix + ages ;

        // 使用布隆过滤器解决缓存穿透问题
        if (!bloomFilter.contains(key)){
            return null;
        }

        //1.添加缓存 先查询redis里是否有数据
        String json = redisTemplate.opsForValue().get(key);
        // 如果有数据将数据解析直接返回
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json, CategoryEntity.class);
        }
        //2.添加分布式锁 防止缓存击穿
        RLock fairLock = redissonClient.getFairLock(gmallCache.lock());
        fairLock.lock();
        try {
            //再次查询缓存，因为在获取锁的过程中，可能有其他请求已经把数据放到缓存中
            String json2 = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(json2)){
                return JSON.parseArray(json2,CategoryEntity.class);
            }
            // 目标方法
            Object result = joinPoint.proceed(joinPoint.getArgs());
            //把目标方法的返回结果集放入缓存，并且释放分布式锁 (防止缓存雪崩)
            if (result!=null){
                int timeout = gmallCache.timeout() + new Random().nextInt(gmallCache.random());
                    redisTemplate.opsForValue().set(key,JSON.toJSONString(result),timeout,TimeUnit.MINUTES);
            }
            return result;
        }finally {
            fairLock.unlock();
        }
    }
}
