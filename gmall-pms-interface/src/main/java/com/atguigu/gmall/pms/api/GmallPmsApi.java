package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {

    @PostMapping("pms/spu/search")
    public ResponseVo<List<SpuEntity>> searchSpuByPage(@RequestBody PageParamVo paramVo);

    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    @GetMapping("pms/category/parent/{id}")
    public ResponseVo<List<CategoryEntity>> parent(@PathVariable Long id);

    @GetMapping("pms/category/subsByPid/{pid}")
    public ResponseVo<List<CategoryEntity>> queryLvl2WithSubsByPid(@PathVariable Long pid);

    @GetMapping("/pms/category/queryParentId/{subId}")
    public ResponseVo<List<CategoryEntity>> queryParentIdBySubId(@PathVariable Long subId);

    @GetMapping("/pms/spudesc/{spuId}")
    public ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/sku/searchSkuBySpuId/{spuId}")
    public ResponseVo<List<SkuEntity>> searchSkuBySpuId(@PathVariable Long spuId);

    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/skuimages/querySkuImg/{skuId}")
    public ResponseVo<List<SkuImagesEntity>> querySkuImgBySkuId(@PathVariable Long skuId);

    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("/pms/skuattrvalue/querySkuSaleAttr/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuSaleAttrBySkuId(@PathVariable Long skuId);

    @GetMapping("/pms/skuattrvalue/querySkuSalesAttrBySpuId/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySkuSalesAttrBySpuId(@PathVariable Long spuId);

    @GetMapping("/pms/skuattrvalue/querySkuSalesAttrMappingSkuIdBySpuId/{spuId}")
    public ResponseVo<String> querySpuSalesAttrMappingSkuIdBySpuId(@PathVariable Long spuId);

    @GetMapping("/pms/skuattrvalue/{id}")
    public ResponseVo<SkuAttrValueEntity> querySkuAttrValueById(@PathVariable("id") Long id);

    @GetMapping("pms/skuattrvalue/searchSkuValue/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> searchSkuValue(@PathVariable Long skuId,
                                                               @RequestParam Long cid);

    @GetMapping("pms/spuattrvalue/searchSpuValue/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> searchSpuValue(@PathVariable Long spuId,
                                                               @RequestParam Long cid);

    @GetMapping("/pms/attrgroup/queryItemGroupVo/{cid}")
    public ResponseVo<List<ItemGroupVo>> queryItemGroupVo(@PathVariable Long cid,
                                                          @RequestParam Long skuId,
                                                          @RequestParam Long spuId);
}
