package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.fegin.SmsClient;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesMapper skuImagesMapper;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private SmsClient smsClient;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo queryPage(PageParamVo paramVo, Long categoryId) {
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
        if (categoryId!=0){
            queryWrapper.eq("category_id",categoryId);
        }
        String key = paramVo.getKey();
        if (StringUtils.isNotBlank(key)){
            queryWrapper.and(t -> t.eq("id",key).or().like("name",key));
        }
        IPage<SpuEntity> page = this.page(paramVo.getPage(),queryWrapper);
        return new PageResultVo(page);
    }

    @GlobalTransactional
    @Override
    public void saveSpuAndSku(SpuVo spuVo) {
        // 添加spu
        Long spuId = saveSpu(spuVo);

        // 添加spu商品介绍图片
        saveSpuDesc(spuVo, spuId);

        // 添加spu商品属性信息
        saveSpuAttr(spuVo, spuId);

        // sku
        saveSku(spuVo, spuId);
    }

    public void saveSku(SpuVo spuVo, Long spuId) {
        List<SkuVo> skus = spuVo.getSkus();
        skus.forEach(skuVo -> {
            skuVo.setSpuId(spuId);
            skuVo.setBrandId(spuVo.getBrandId());
            skuVo.setCategoryId(spuVo.getCategoryId());
            List<String> images = skuVo.getImages();
            // 如果没有默认图片那么就将第一张图片设置为默认图片
            if (!CollectionUtils.isEmpty(images)){
                if (!StringUtils.isNotBlank(skuVo.getDefaultImage())){
                    skuVo.setDefaultImage(images.get(0));
                }

            }
            skuMapper.insert(skuVo);

            // 添加sku图片
            if (!CollectionUtils.isEmpty(images)){
                SkuImagesEntity skuImage = new SkuImagesEntity();
                skuImage.setDefaultStatus(1);
                skuImage.setSkuId(skuVo.getId());
                skuImage.setUrl(StringUtils.join(images,","));
                skuImagesMapper.insert(skuImage);
            }

            // 添加sku属性信息
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(skuAttrValueEntity -> {
                    skuAttrValueEntity.setSkuId(skuVo.getId());
                });
                skuAttrValueService.saveBatch(saleAttrs);
            }

            // sms
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo,skuSaleVo);
            skuSaleVo.setSkuId(skuVo.getId());
            smsClient.saveSkuBounds(skuSaleVo);
        });
    }

    public void saveSpuAttr(SpuVo spuVo, Long spuId) {
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            spuAttrValueService.saveBatch(baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValue = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo,spuAttrValue);
                spuAttrValue.setSpuId(spuId);
                return spuAttrValue;
            }).collect(Collectors.toList()));
        }
    }

    public void saveSpuDesc(SpuVo spuVo, Long spuId) {
        List<String> spuImages = spuVo.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)){
            String img = StringUtils.join(spuImages,",");
            SpuDescEntity spuDesc = new SpuDescEntity();
            spuDesc.setSpuId(spuId);
            spuDesc.setDecript(img);
            spuDescMapper.insert(spuDesc);
        }
    }

    public Long saveSpu(SpuVo spuVo) {
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        baseMapper.insert(spuVo);
        return spuVo.getId();
    }

}