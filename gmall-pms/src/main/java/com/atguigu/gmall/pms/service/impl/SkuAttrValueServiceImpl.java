package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.springframework.util.CollectionUtils;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuMapper skuMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SkuAttrValueEntity> getSkuValueBySkuIdAndCid(Long skuId, Long cid) {
        List<AttrEntity> attrListByCid = attrService.getAttrListByCid(cid, 0, 1);
        if (CollectionUtils.isEmpty(attrListByCid)){
            return null;
        }
        List<Long> collect = attrListByCid.stream().map(AttrEntity::getId).collect(Collectors.toList());
        return baseMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id",skuId).in("attr_id",collect));
    }

    @Override
    public List<SaleAttrValueVo> querySkuSalesAttrBySpuId(Long spuId) {
        // 1.先根据spu查询skuIds
        List<SkuEntity> skuEntities = skuMapper.selectList(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuEntities)){
            return null;
        }
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());

        // 2.根据skuIds查询sku销售属性
        List<SkuAttrValueEntity> skuAttrValues = baseMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().in("sku_id", skuIds));
        if (CollectionUtils.isEmpty(skuAttrValues)){
           return null;
        }
        // 3.使用steam根据attrId分组
        Map<Long, List<SkuAttrValueEntity>> map = skuAttrValues.stream().collect(Collectors.groupingBy(SkuAttrValueEntity::getAttrId));
        // 4.封装代码成{attrId: 3, attrName："机身颜色", attrValues: ["暗夜黑", "白天白"]},
        List<SaleAttrValueVo> saleAttrValueVos = new ArrayList<>();
        map.forEach((attrId ,attrValues) -> {
            SaleAttrValueVo saleAttrValueVo = new SaleAttrValueVo();
            saleAttrValueVo.setAttrId(attrId);
            saleAttrValueVo.setAttrName(attrValues.get(0).getAttrName());
            Set<String> collect = attrValues.stream().map(SkuAttrValueEntity::getAttrValue).collect(Collectors.toSet());
            saleAttrValueVo.setAttrValues(collect);
            saleAttrValueVos.add(saleAttrValueVo);
        });
        return saleAttrValueVos;
    }


    @Override
    public String querySkuSalesAttrMappingSkuIdBySpuId(Long spuId) {
        // 1.先根据spu查询skuIds
        List<SkuEntity> skuEntities = skuMapper.selectList(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuEntities)){
            return null;
        }
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        // 2.           {"白天白, 8G, 256G": 10, "白天白, 12G, 128G": 20}
        // 查询映射关系
        List<Map<String, Object>> maps = baseMapper.selectSkuSalesAttrMappingSkuId(skuIds);
        if (CollectionUtils.isEmpty(maps)){
            return null;
        }
        Map<String,Long> mapping = maps.stream().collect(Collectors.toMap(map -> map.get("attr_values").toString(),map -> (Long)map.get("sku_id")));

        return JSON.toJSONString(mapping);
    }

    @Override
    public List<SkuAttrValueEntity> querySkuSaleAttrBySkuId(Long skuId) {
        return baseMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId));
    }

    @Override
    public List<SkuAttrValueEntity> querySkuSalesAttrBySpuId2(Long spuId) {
        // 1.先根据spu查询skuIds
        List<SkuEntity> skuEntities = skuMapper.selectList(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuEntities)){
            return null;
        }
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        List<SkuAttrValueEntity> skuAttrValueEntityList = baseMapper.querySkuSalesAttrBySkuId2(skuIds);
        return skuAttrValueEntityList;
    }
}