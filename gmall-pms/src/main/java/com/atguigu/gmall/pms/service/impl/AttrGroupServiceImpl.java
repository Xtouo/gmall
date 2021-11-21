package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.vo.AttrValueVo;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SpuAttrValueMapper spuAttrValueMapper;

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

    @Override
    public List<ItemGroupVo> queryItemGroupVoByCidAndSkuIdAndSpuId(Long cid, Long skuId, Long spuId) {
        // 1.根据分类查询属性分组信息
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));
        if (CollectionUtils.isEmpty(attrGroupEntities)){
           return null;
        }
        // 2.
        List<ItemGroupVo> itemGroupVos = new ArrayList<>();
        itemGroupVos.addAll(attrGroupEntities.stream().map(attrGroupEntity -> {
            ItemGroupVo itemGroupVo = new ItemGroupVo();
            // 根据分组id查询出对应的属性
            List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupEntity.getId()));
            if (!CollectionUtils.isEmpty(attrEntities)){
                itemGroupVo.setId(attrGroupEntity.getId());
                itemGroupVo.setName(attrGroupEntity.getName());
            }
            // 获取属性的Ids
            List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
            if (attrIds!=null && attrIds.size() > 0){
                List<SkuAttrValueEntity> skuAttrValueEntityList = skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id",attrIds));
                // 根据attrIds和skuId查询sku属性
                if (!CollectionUtils.isEmpty(skuAttrValueEntityList)){
                    itemGroupVo.setAttrs(skuAttrValueEntityList.stream().map(skuAttrValueEntity -> {
                        AttrValueVo attrValueVo = new AttrValueVo();
                        BeanUtils.copyProperties(skuAttrValueEntity,attrValueVo);
                        return attrValueVo;
                    }).collect(Collectors.toList()));
                }
                // 根据attrIds和spuId查询sku属性
                List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrValueMapper.selectList(new QueryWrapper<SpuAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));
                if (!CollectionUtils.isEmpty(spuAttrValueEntities)){
                    itemGroupVo.setAttrs(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                        AttrValueVo attrValueVo = new AttrValueVo();
                        BeanUtils.copyProperties(spuAttrValueEntity,attrValueVo);
                        return attrValueVo;
                    }).collect(Collectors.toList()));
                }
            }
            return itemGroupVo;
        }).collect(Collectors.toList()));

        return itemGroupVos;
    }
}