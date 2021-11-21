package com.atguigu.gmall.index.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class DistributedLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Timer timer;

    public boolean lock(String lockName , String uuid ,Integer timeout){
        String script = "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1 " +
                "then " +
                "redis.call('hincrby', KEYS[1], ARGV[1], 1) redis.call('expire', KEYS[1], ARGV[2]) " +
                "return 1 " +
                "else " +
                "return 0 " +
                "end";
        Boolean lock = this.redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList("lock"), uuid, timeout.toString());
        //false表示没有获取到锁
        while (!lock){
            try {
                Thread.sleep(80);
                this.lock(lockName,uuid,timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        renewExpire(lockName,uuid,timeout);
        return true;
    }


    public void unlock(String lockName,String uuid){
        String script = "if redis.call('hexists', KEYS[1], ARGV[1]) == 0 " +
                        "then " +
                        "return nil " +
                        "elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0 " +
                        "then  " +
                        "return redis.call('del', KEYS[1]) " +
                        "else " +
                        "return 0 " +
                        "end";
        Long flag = this.redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(lockName), uuid);
        // flag等于null表示没有这个锁 恶意释放锁
        if (flag == null){
            throw new RuntimeException("要释放的锁不存在!");
        }
        if (flag == 1){
            timer.cancel();;
        }
    }

    public void renewExpire(String lockName , String uuid , Integer timeout){
        //判断是否时自己的锁
        String script = "if redis.call('hexists', KEYS[1], ARGV[1]) == 1 " +
                "then " +
                    "redis.call('expire', KEYS[1], ARGV[2]) " +
                    "return 1 " +
                    "else " +
                    "return 0 " +
                    "end";
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                redisTemplate.execute(new DefaultRedisScript<>(script,Boolean.class),Arrays.asList(lockName),uuid,timeout);
            }
        },timeout * 1000 / 3, timeout * 1000 / 3);
    }
}
