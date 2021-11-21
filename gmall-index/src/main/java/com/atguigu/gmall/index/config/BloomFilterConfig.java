package com.atguigu.gmall.index.config;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallPmsClient pmsClient;

    private static final String KEY_PREFIX = "index:cates:";

    @Bean
    public RBloomFilter<String> bloomFilter(){
        RBloomFilter<String> bloomFilter = this.redissonClient.getBloomFilter("index:bf");
        bloomFilter.tryInit(2000l, 0.03);

        // 向布隆过滤器中初始化数据
        ResponseVo<List<CategoryEntity>> responseVo = this.pmsClient.parent(0l);
        List<CategoryEntity> categoryEntities = responseVo.getData();
        if (!CollectionUtils.isEmpty(categoryEntities)){
            categoryEntities.forEach(categoryEntity -> {
                bloomFilter.add(KEY_PREFIX + categoryEntity.getId());
            });
        }

        return bloomFilter;
    }
}
