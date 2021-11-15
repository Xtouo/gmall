package com.atguigu.gmall.search.listener;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValueVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RabbitMqListener {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private GoodsRepository goodsRepository;


    @RabbitListener(queues = {"PMS_SPU_QUEUE"})
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue("SEARCH_INSERT_QUEUE"),
//            exchange = @Exchange(value = "PMS_SPU_EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.DIRECT),
//            key = {"msg.spu"}
//    ))
    public void syncSave(Long spuId, Channel channel, Message message) throws IOException {
        try {
            ResponseVo<List<SkuEntity>> responseSkuVo = gmallPmsClient.searchSkuBySpuId(spuId);
            List<SkuEntity> skuList = responseSkuVo.getData();
            if (!CollectionUtils.isEmpty(skuList)) {
                SpuEntity spuEntity = gmallPmsClient.querySpuById(spuId).getData();
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

                //保存es数据
                goodsRepository.saveAll(goodsList);
            }

            // 确认消息：1-固定写法 2-批量确认，一般使用false
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            // getRedelivered判断消息是否重新发送过
            if (message.getMessageProperties().getRedelivered()){
                // 可以记录日志 或者 记录到数据库
                // 如果重新发送过 那么拒绝消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                // 不确认消息 3-重新入队，可以重试  如果把它设置false：如果当前队列绑定了死信队列，会进入死信队列，否则消息会丢弃
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }


}
