package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ItemVo {

    // 商品详情页面包屑所需字段
    // 三级分类 id name     V
    private List<CategoryEntity> categories;
    // 品牌               V
    private Long brandId;
    private String brandName;
    // spu信息            V
    private Long spuId;
    private String spuName;

    // 中间主体部分所需字段   V
    private Long skuId;
    private String title;
    private String subTitle;
    private BigDecimal price;
    private Integer weight;
    private String defaultImage;

    // sku图片列表          V
    private List<SkuImagesEntity> images;

    // 营销信息             V
    private List<ItemSaleVo> sales;

    // 是否有货             V
    private Boolean store = false;

    // 销售类型的规格参数列表          V
    // [
    //  {attrId: 3, attrName："机身颜色", attrValues: ["暗夜黑", "白天白"]},
    //  {attrId: 4, attrName："运行内存", attrValues: ["8G", "12G"]},
    //  {attrId: 5, attrName："机身存储", attrValues: ["128G", "256G"]}
    // ]
    private List<SaleAttrValueVo> saleAttrs;

    // 当前sku的销售属性：{3: "白天白", 4: "8G", 5: "256G"}  V
    private Map<Long, String> saleAttr;

    // 销售属性组合与skuId的映射关系：               V
    // {"白天白, 8G, 256G": 10, "白天白, 12G, 128G": 20}
    private String skuJsons;

    // 海报信息                     V
    private List<String> spuImages;

    // 规格参数分组列表
    private List<ItemGroupVo> groups;
}
