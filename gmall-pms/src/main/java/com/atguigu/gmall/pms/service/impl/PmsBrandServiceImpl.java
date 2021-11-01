package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsBrandMapper;
import com.atguigu.gmall.pms.entity.PmsBrandEntity;
import com.atguigu.gmall.pms.service.PmsBrandService;


@Service("pmsBrandService")
public class PmsBrandServiceImpl extends ServiceImpl<PmsBrandMapper, PmsBrandEntity> implements PmsBrandService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsBrandEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsBrandEntity>()
        );

        return new PageResultVo(page);
    }

}