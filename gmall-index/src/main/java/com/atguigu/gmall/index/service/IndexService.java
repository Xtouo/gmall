package com.atguigu.gmall.index.service;

import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    public List<CategoryEntity> queryLvl1Cates(){
        return gmallPmsClient.parent(0L).getData();
    }

    public List<CategoryEntity> queryLv23Cates(Long pid) {
        List<CategoryEntity> categoryEntities = gmallPmsClient.queryLvl2WithSubsByPid(pid).getData();
        return categoryEntities;
    }
}
