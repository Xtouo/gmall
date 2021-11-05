package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Autowired
    AttrService attrService;

    @Override
    public List<AttrGroupEntity> withattrs(Long catId) {
        List<AttrGroupEntity> attrGroups = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id",catId));
        if (!CollectionUtils.isEmpty(attrGroups)) {
            for (AttrGroupEntity attrGroup : attrGroups) {
                List<AttrEntity> attrList = attrService.list(new QueryWrapper<AttrEntity>()
                        .eq("category_id", catId)
                        .eq("group_id",attrGroup.getId())
                        .eq("type",1));
                attrGroup.setAttrEntities(attrList);
                }
            }
        return attrGroups;
    }

}