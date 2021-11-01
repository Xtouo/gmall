package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsSkuMapper;
import com.atguigu.gmall.pms.entity.PmsSkuEntity;
import com.atguigu.gmall.pms.service.PmsSkuService;


@Service("pmsSkuService")
public class PmsSkuServiceImpl extends ServiceImpl<PmsSkuMapper, PmsSkuEntity> implements PmsSkuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsSkuEntity>()
        );

        return new PageResultVo(page);
    }

}