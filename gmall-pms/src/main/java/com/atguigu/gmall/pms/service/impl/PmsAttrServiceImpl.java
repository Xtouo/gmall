package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsAttrMapper;
import com.atguigu.gmall.pms.entity.PmsAttrEntity;
import com.atguigu.gmall.pms.service.PmsAttrService;


@Service("pmsAttrService")
public class PmsAttrServiceImpl extends ServiceImpl<PmsAttrMapper, PmsAttrEntity> implements PmsAttrService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsAttrEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsAttrEntity>()
        );

        return new PageResultVo(page);
    }

}