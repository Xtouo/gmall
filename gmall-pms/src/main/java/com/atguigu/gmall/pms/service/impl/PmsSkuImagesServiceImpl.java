package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsSkuImagesMapper;
import com.atguigu.gmall.pms.entity.PmsSkuImagesEntity;
import com.atguigu.gmall.pms.service.PmsSkuImagesService;


@Service("pmsSkuImagesService")
public class PmsSkuImagesServiceImpl extends ServiceImpl<PmsSkuImagesMapper, PmsSkuImagesEntity> implements PmsSkuImagesService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsSkuImagesEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsSkuImagesEntity>()
        );

        return new PageResultVo(page);
    }

}