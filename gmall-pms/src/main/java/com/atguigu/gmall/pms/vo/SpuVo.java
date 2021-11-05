package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuVo extends SpuEntity {
    // 商品图片
    private List<String> spuImages;
    // 商品属性
    private List<SpuAttrValueVo> baseAttrs;
    //
    private List<SkuVo> skus;
}
