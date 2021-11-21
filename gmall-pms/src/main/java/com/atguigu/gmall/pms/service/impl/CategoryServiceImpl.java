package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> queryLvl2WithSubsByPid(Long pid) {
        return baseMapper.categoryByPid(pid);
    }

    @Override
    public List<CategoryEntity> queryParentIdBySubId(Long subId) {
        // 三级分类
        CategoryEntity categoryEntity3 = baseMapper.selectById(subId);
        if (categoryEntity3 == null){
            return null;
        }
        // 二级分类
        CategoryEntity categoryEntity2 = baseMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id",categoryEntity3.getParentId()));
        // 一级分类
        CategoryEntity categoryEntity1 = baseMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id",categoryEntity2.getParentId()));

        return Arrays.asList(categoryEntity1,categoryEntity2,categoryEntity3);
    }
}