package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * 
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-11-01 15:29:23
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValueEntity> {

    List<Map<String, Object>> selectSkuSalesAttrMappingSkuId(@Param("skuIds") List<Long> skuIds);

    List<SkuAttrValueEntity> querySkuSalesAttrBySkuId2(@Param("skuIds") List<Long> skuIds);
}
