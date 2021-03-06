package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SpuVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescService spuDescService;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );
        return new PageResultVo(page);
    }

    @Override
    public PageResultVo queryPage(PageParamVo paramVo, Long categoryId) {
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
        if (categoryId != 0) {
            queryWrapper.eq("category_id", categoryId);
        }
        String key = paramVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(t -> t.eq("id", key).or().like("name", key));
        }
        IPage<SpuEntity> page = this.page(paramVo.getPage(), queryWrapper);
        return new PageResultVo(page);
    }

    @GlobalTransactional
    @Override
    public void saveSpuAndSku(SpuVo spuVo) {
        // ??????spu
        SpuEntity spuEntity = new SpuEntity();
        BeanUtils.copyProperties(spuVo,spuEntity);
        spuEntity.setCreateTime(new Date());
        spuEntity.setUpdateTime(spuVo.getCreateTime());
        this.save(spuEntity);
        Long spuId = spuEntity.getId();

        // ??????spu??????????????????
        //saveSpuDesc(spuVo, spuId);
        this.spuDescService.saveSpuDesc(spuVo, spuId);

        // ??????spu??????????????????
        //saveSpuAttr(spuVo, spuId);
        this.spuAttrValueService.saveSpuAttr(spuVo, spuId);
        // sku
        //saveSku(spuVo, spuId);
        this.skuService.saveSku(spuVo, spuId);

        //??????mq????????????
        rabbitTemplate.convertAndSend("PMS_SPU_EXCHANGE","msg.spu",spuId);
    }

    @Override
    public Long saveSpu(SpuVo spuVo) {
        return null;
    }


}
