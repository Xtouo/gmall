package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValueVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class GmallSearchApplicationTests {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    void contextLoads() {

        if (!restTemplate.indexExists(Goods.class)) {
            restTemplate.createIndex(Goods.class);
            restTemplate.putMapping(Goods.class);
        }



        Integer pageNum = 1;
        Integer pageSize = 10;

        do {
            PageParamVo pageParamVo = new PageParamVo(pageNum,pageSize,null);
            ResponseVo<List<SpuEntity>> listResponseVo = gmallPmsClient.searchSpuByPage(pageParamVo);
            List<SpuEntity> spuList = listResponseVo.getData();
            if (!CollectionUtils.isEmpty(spuList)){
                spuList.forEach(spuEntity -> {
                    ResponseVo<List<SkuEntity>> responseSkuVo = gmallPmsClient.searchSkuBySpuId(spuEntity.getId());
                    List<SkuEntity> skuList = responseSkuVo.getData();
                    if (!CollectionUtils.isEmpty(skuList)) {
                        BrandEntity brandEntity = gmallPmsClient.queryBrandById(spuEntity.getBrandId()).getData();
                        CategoryEntity categoryEntity = gmallPmsClient.queryCategoryById(spuEntity.getCategoryId()).getData();
                        List<SpuAttrValueEntity> spuAttr = gmallPmsClient.searchSpuValue(spuEntity.getId(), spuEntity.getCategoryId()).getData();
                        List<Goods> goodsList = skuList.stream().map(skuEntity -> {
                            Goods goods = new Goods();
                            goods.setSkuId(skuEntity.getId());
                            goods.setTitle(skuEntity.getTitle());
                            goods.setSubtitle(skuEntity.getSubtitle());
                            goods.setPrice(skuEntity.getPrice().doubleValue());
                            goods.setDefaultImage(skuEntity.getDefaultImage());
                            goods.setCreateTime(spuEntity.getCreateTime());

                            List<WareSkuEntity> wareList = gmallWmsClient.queryWareSkusBySkuId(skuEntity.getId()).getData();
                            if (!CollectionUtils.isEmpty(wareList)) {
                                goods.setSales(wareList.stream().map(WareSkuEntity::getSales).reduce(((a, b) -> a + b)).get());
                                goods.setStore(wareList.stream().allMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                            }

                            if (brandEntity != null){
                                goods.setBrandId(brandEntity.getId());
                                goods.setLogo(brandEntity.getLogo());
                                goods.setBrandName(brandEntity.getName());
                            }

                            if (categoryEntity != null){
                                goods.setCategoryId(categoryEntity.getId());
                                goods.setCategoryName(categoryEntity.getName());
                            }

                            List<SearchAttrValueVo> searchAttrs = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(spuAttr)){
                                searchAttrs.addAll(spuAttr.stream().map(spuAttrValueEntity -> {
                                    SearchAttrValueVo searchAttrValueVo = new SearchAttrValueVo();
                                    BeanUtils.copyProperties(spuAttrValueEntity,searchAttrValueVo);
                                    return searchAttrValueVo;
                                }).collect(Collectors.toList()));
                            }

                            List<SkuAttrValueEntity> skuAttr = gmallPmsClient.searchSkuValue(skuEntity.getId(), spuEntity.getCategoryId()).getData();
                            if(!CollectionUtils.isEmpty(skuAttr)){
                                searchAttrs.addAll(skuAttr.stream().map(skuAttrValueEntity -> {
                                    SearchAttrValueVo searchAttrValueVo = new SearchAttrValueVo();
                                    BeanUtils.copyProperties(skuAttrValueEntity,searchAttrValueVo);
                                    return searchAttrValueVo;
                                }).collect(Collectors.toList()));
                            }
                            goods.setSearchAttrs(searchAttrs);
                            return goods;
                        }).collect(Collectors.toList());

                        goodsRepository.saveAll(goodsList);
                    }
                });
            }
            pageNum++;
            pageSize = spuList.size();
        }while (pageSize == 100);
    }
}
