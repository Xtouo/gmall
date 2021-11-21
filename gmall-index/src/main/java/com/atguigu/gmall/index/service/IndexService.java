package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.utils.DistributedLock;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "index:cates:";

    public List<CategoryEntity> queryLvl1Cates(){
        return gmallPmsClient.parent(0L).getData();
    }

    // 封装成注解后
    @GmallCache(prefix = KEY_PREFIX,timeout = 129600,random = 14400,lock = "index:cates:lock:")
    public List<CategoryEntity> queryLv23Cates(Long pid) {
            List<CategoryEntity> categoryEntities = gmallPmsClient.queryLvl2WithSubsByPid(pid).getData();
            return categoryEntities;
    }

    // 封装成注解前
    public List<CategoryEntity> queryLv23Cates2(Long pid) {
        //1.添加缓存 先查询redis里是否有数据
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        // 如果有数据将数据解析直接返回
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json,CategoryEntity.class);
        }

        RLock fairLock = redissonClient.getFairLock("index:cates:lock:" + pid);
        fairLock.lock();
        List<CategoryEntity> categoryEntities;
        try {
            //再次查询缓存，因为在获取锁的过程中，可能有其他请求已经把数据放到缓存中
            String json2 = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
            if (StringUtils.isNotBlank(json2)){
                return JSON.parseArray(json2,CategoryEntity.class);
            }
            //2.调用远程接口从数据库查询数据 并保存到redis
            categoryEntities = gmallPmsClient.queryLvl2WithSubsByPid(pid).getData();
            if (CollectionUtils.isEmpty(categoryEntities)){
                //如果数据为null也保存到redis 防止缓存穿透
                redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities),5,TimeUnit.MINUTES);
            }else {
                // 添加随机过期时间防止缓存雪崩
                redisTemplate.opsForValue().set(KEY_PREFIX + pid ,JSON.toJSONString(categoryEntities),90 + new Random().nextInt(10),TimeUnit.DAYS);
            }
            return categoryEntities;

        } finally {
            fairLock.unlock();
        }
    }

    // 分布式锁案例
    public void testLock() {
        String uuid = UUID.randomUUID().toString();
        // 加锁 set key value ex 3 nx  设置过期时间防止死锁
        Boolean flag = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (!flag){
            try {
                // 重试
                Thread.sleep(100);
                this.testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //this.redisTemplate.expire("lock", 20, TimeUnit.SECONDS);
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)){
                this.redisTemplate.opsForValue().set("num", "1");
            }else {
                this.redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(num) + 1));
            }

            // 解锁 finally  这种解锁方式会导致误解锁 在判断时锁如果锁过期 刚好其他线程获取到锁就会导致误删除
//            if (StringUtils.equals(uuid, this.redisTemplate.opsForValue().get("lock"))){
//                this.redisTemplate.delete("lock");
//            }
            // 使用lua脚本可以使判断和删除保证原子性，防止误删除
            String lua ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            this.redisTemplate.execute(new DefaultRedisScript<>(lua,Boolean.class), Arrays.asList("lock"),uuid);
        }
    }

    // 分布式锁案例 基于上面 再次优化 可重入和自动续期
    public void testLock2() {
        String uuid = UUID.randomUUID().toString();

        boolean lock = distributedLock.lock("lock", uuid, 30);
        if (lock){
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)){
                this.redisTemplate.opsForValue().set("num", "1");
            }else {
                this.redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(num) + 1));
            }
            distributedLock.unlock("lock",uuid);
        }
    }

    // 使用redisson框架实现分布式锁
    public void testLock3() {
        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        try {
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)){
                this.redisTemplate.opsForValue().set("num", "1");
            }else {
                this.redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(num) + 1));
            }
        } finally {
            lock.unlock();
        }
    }
}


