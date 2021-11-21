package com.atguigu.gmall.index.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) //作用于方法上
@Retention(RetentionPolicy.RUNTIME) //运行时注解
@Documented
public @interface GmallCache {
    /**
     * 缓存key的前缀
     * @return
     */
    String prefix() default "gmall:";

    /**
     * 缓存的过期时间，默认5min
     * @return
     */
    int timeout() default 5;

    /**
     * 为了防止缓存雪崩，给缓存时间添加随机值，这里可以指定随机值范围，默认5min
     * @return
     */
    int random() default 5;

    /**
     * 为了防止缓存击穿，给缓存添加分布式锁，这里可以指定分布式锁的前缀
     * 分布式锁的key = lock:{方法参数}
     * @return
     */
    String lock() default "lock:";
}
