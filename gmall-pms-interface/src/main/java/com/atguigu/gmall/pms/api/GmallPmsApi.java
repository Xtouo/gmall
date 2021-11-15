package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
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

    @GetMapping("pms/sku/searchSkuBySpuId/{spuId}")
    public ResponseVo<List<SkuEntity>> searchSkuBySpuId(@PathVariable Long spuId);

    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/skuattrvalue/searchSkuValue/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> searchSkuValue(@PathVariable Long skuId,
                                                               @RequestParam Long cid);

    @GetMapping("pms/spuattrvalue/searchSpuValue/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> searchSpuValue(@PathVariable Long spuId,
                                                               @RequestParam Long cid);
}
