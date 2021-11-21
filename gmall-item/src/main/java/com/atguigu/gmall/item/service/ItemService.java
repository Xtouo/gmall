package com.atguigu.gmall.item.service;

import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TemplateEngine templateEngine;

    public ItemVo loadData(Long skuId) {
        ItemVo itemVo = new ItemVo();
        //1.根据skuId查询sku
        CompletableFuture<SkuEntity> skuFuture = CompletableFuture.supplyAsync(() -> {
            SkuEntity skuEntity = pmsClient.querySkuById(skuId).getData();
            if (skuEntity == null) {
                throw new GmallException("商品不存在！");
            }
            itemVo.setSkuId(skuEntity.getId());
            itemVo.setTitle(skuEntity.getTitle());
            itemVo.setSubTitle(skuEntity.getSubtitle());
            itemVo.setPrice(skuEntity.getPrice());
            itemVo.setWeight(skuEntity.getWeight());
            itemVo.setDefaultImage(skuEntity.getDefaultImage());
            return skuEntity;
        }, executorService);

        //2.根据三级分类的id查询一二三级分类 V
        CompletableFuture<Void> catesFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            List<CategoryEntity> categoryEntities = pmsClient.queryParentIdBySubId(skuEntity.getCategoryId()).getData();
            itemVo.setCategories(categoryEntities);
        }, executorService);

        //3.根据品牌id查询品牌		V
        CompletableFuture<Void> BrandFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            BrandEntity brandEntity = pmsClient.queryBrandById(skuEntity.getBrandId()).getData();
            if (brandEntity != null) {
                itemVo.setBrandId(brandEntity.getId());
                itemVo.setBrandName(brandEntity.getName());
            }
        }, executorService);

        //4.根据spuId查询spu
        CompletableFuture<Void> spuFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            SpuEntity spuEntity = pmsClient.querySpuById(skuEntity.getSpuId()).getData();
            if (skuEntity != null) {
                itemVo.setSpuId(spuEntity.getId());
                itemVo.setSpuName(spuEntity.getName());
            }
        }, executorService);

        //5.根据skuId查询sku的图片列表 V
        CompletableFuture<Void> skuimgFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = pmsClient.querySkuImgBySkuId(skuId).getData();
            itemVo.setImages(skuImagesEntities);
        }, executorService);

        //6.根据skuId查询营销信息		V
        CompletableFuture<Void> itemSableFuture = CompletableFuture.runAsync(() -> {
            List<ItemSaleVo> itemSableVos = smsClient.ItemSalesBySkuId(skuId).getData();
            itemVo.setSales(itemSableVos);
        }, executorService);

        //7.根据skuId查询库存信息		V
        CompletableFuture<Void> wareSkuFuture = CompletableFuture.runAsync(() -> {
            List<WareSkuEntity> wareSkuEntitys = wmsClient.queryWareSkusBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntitys)) {
                itemVo.setStore(wareSkuEntitys.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
            }
        }, executorService);

        //8.根据spuId查询spu下所有sku的销售属性  saleAttrs  V
        CompletableFuture<Void> saleAttrFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            List<SaleAttrValueVo> saleAttrValueVos = pmsClient.querySkuSalesAttrBySpuId(skuEntity.getSpuId()).getData();
            itemVo.setSaleAttrs(saleAttrValueVos);
        }, executorService);

        //9.根据skuId查询当前sku的销售属性				V
        //当前sku的销售属性：{3: "白天白", 4: "8G", 5: "256G"}
        CompletableFuture<Void> skuAttrFuture = CompletableFuture.runAsync(() -> {
            List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.querySkuSaleAttrBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                itemVo.setSaleAttr(skuAttrValueEntities.stream().collect(Collectors.toMap(skuAttrValueEntity -> (Long) skuAttrValueEntity.getAttrId(), skuAttrValueEntity -> skuAttrValueEntity.getAttrValue())));
            }
        }, executorService);

        //10.根据spuId查询spu下所有销售属性组合与skuId的映射关系 V
        CompletableFuture<Void> skuAttrMappingFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            String json = pmsClient.querySpuSalesAttrMappingSkuIdBySpuId(skuEntity.getSpuId()).getData();
            itemVo.setSkuJsons(json);
        }, executorService);

        //11.根据spuId查询描述信息  V
        CompletableFuture<Void> spuDescFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            SpuDescEntity spuDescEntity = pmsClient.querySpuDescById(skuEntity.getSpuId()).getData();
            if (spuDescEntity != null) {
                String[] split = StringUtils.split(spuDescEntity.getDecript(), ",");
                if (split == null) {
                    itemVo.setSpuImages(Arrays.asList(spuDescEntity.getDecript()));
                } else {
                    itemVo.setSpuImages(Arrays.asList(split));
                }
            }
        }, executorService);

        //12.根据cid spuId skuId查询规格参数分组及组下的规格参数和值
        CompletableFuture<Void> itemGroupFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            List<ItemGroupVo> itemGroupVos = pmsClient.queryItemGroupVo(skuEntity.getCategoryId(), skuEntity.getId(), skuEntity.getSpuId()).getData();
            itemVo.setGroups(itemGroupVos);
        }, executorService);

        CompletableFuture.allOf(catesFuture,saleAttrFuture,skuAttrFuture,skuAttrMappingFuture
                                ,skuimgFuture,spuDescFuture,spuFuture,wareSkuFuture,
                                itemGroupFuture,itemSableFuture,BrandFuture).join();

        creatStaticSource(itemVo);

        return itemVo;
    }

    private void creatStaticSource(ItemVo itemVo){

        executorService.execute(()->{
            Context context = new Context();
            context.setVariable("itemVo",itemVo);

            try (PrintWriter printWriter = new PrintWriter("D:\\gmall-item-static\\" + itemVo.getSkuId() + ".html")){
                templateEngine.process("item",context,printWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
