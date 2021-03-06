package com.atguigu.gmall.pms.controller;

import java.util.List;
import java.util.Map;

import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * sku销售属性&值
 *
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-11-01 15:29:23
 */
@Api(tags = "sku销售属性&值 管理")
@RestController
@RequestMapping("pms/skuattrvalue")
public class SkuAttrValueController {

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @GetMapping("querySkuSalesAttrMappingSkuIdBySpuId/{spuId}")
    public ResponseVo<String> querySpuSalesAttrMappingSkuIdBySpuId(@PathVariable Long spuId){
        String json = skuAttrValueService.querySkuSalesAttrMappingSkuIdBySpuId(spuId);
        return ResponseVo.ok(json);
    }

    @GetMapping("querySkuSalesAttrBySpuId/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySkuSalesAttrBySpuId(@PathVariable Long spuId){
        List<SaleAttrValueVo> saleAttrValueVos = skuAttrValueService.querySkuSalesAttrBySpuId(spuId);
        return ResponseVo.ok(saleAttrValueVos);
    }

    @GetMapping("test/{spuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuSalesAttrBySpuId2(@PathVariable Long spuId){
        List<SkuAttrValueEntity> saleAttrValueVos = skuAttrValueService.querySkuSalesAttrBySpuId2(spuId);
        return ResponseVo.ok(saleAttrValueVos);
    }

    @GetMapping("searchSkuValue/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> searchSkuValue(@PathVariable Long skuId,Long cid){
        List<SkuAttrValueEntity> skuAttrValueEntityList = skuAttrValueService.getSkuValueBySkuIdAndCid(skuId,cid);
        return ResponseVo.ok(skuAttrValueEntityList);
    }

    @GetMapping("querySkuSaleAttr/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuSaleAttrBySkuId(@PathVariable Long skuId){
        List<SkuAttrValueEntity> skuAttrValueEntitys = skuAttrValueService.querySkuSaleAttrBySkuId(skuId);
        return ResponseVo.ok(skuAttrValueEntitys);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySkuAttrValueByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = skuAttrValueService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuAttrValueEntity> querySkuAttrValueById(@PathVariable("id") Long id){
		SkuAttrValueEntity skuAttrValue = skuAttrValueService.getById(id);
        return ResponseVo.ok(skuAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuAttrValueEntity skuAttrValue){
		skuAttrValueService.save(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuAttrValueEntity skuAttrValue){
		skuAttrValueService.updateById(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		skuAttrValueService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
