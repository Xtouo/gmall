package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuSaleVo {

    private Long skuId;

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
}
