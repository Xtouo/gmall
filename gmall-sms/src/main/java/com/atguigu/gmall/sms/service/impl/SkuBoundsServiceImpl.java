package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionMapper skuFullReductionMapper;

    @Autowired
    private SkuLadderMapper skuLadderMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Transactional
    @Override
    public void saveSkuSaleVo(SkuSaleVo skuSaleVo) {
        // 添加积分优惠
        SkuBoundsEntity skuBounds = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleVo,skuBounds);
        List<Integer> work = skuSaleVo.getWork();
        if (!CollectionUtils.isEmpty(work)){
            skuBounds.setWork(work.get(0)*8+work.get(1)*4+work.get(2)*2+work.get(3)*1);
        }
        baseMapper.insert(skuBounds);

        // 添加满减
        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleVo,skuFullReduction);
        skuFullReduction.setAddOther(skuSaleVo.getFullAddOther());
        skuFullReductionMapper.insert(skuFullReduction);

        // 添加打折
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleVo,skuLadder);
        skuLadder.setAddOther(skuSaleVo.getLadderAddOther());
        skuLadderMapper.insert(skuLadder);
    }
}