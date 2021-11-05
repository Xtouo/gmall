package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVo extends SkuEntity {

    // 积分优惠字段
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 打折字段
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;

    // 满减字段
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    // sku的图片列表
    private List<String> images;

    // 销售属性及值
    private List<SkuAttrValueEntity> saleAttrs;
}
