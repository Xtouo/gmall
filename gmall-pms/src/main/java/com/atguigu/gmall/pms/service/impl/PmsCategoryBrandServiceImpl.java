package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsCategoryBrandMapper;
import com.atguigu.gmall.pms.entity.PmsCategoryBrandEntity;
import com.atguigu.gmall.pms.service.PmsCategoryBrandService;


@Service("pmsCategoryBrandService")
public class PmsCategoryBrandServiceImpl extends ServiceImpl<PmsCategoryBrandMapper, PmsCategoryBrandEntity> implements PmsCategoryBrandService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsCategoryBrandEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsCategoryBrandEntity>()
        );

        return new PageResultVo(page);
    }

}