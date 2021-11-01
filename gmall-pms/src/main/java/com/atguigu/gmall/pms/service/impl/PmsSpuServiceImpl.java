package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsSpuMapper;
import com.atguigu.gmall.pms.entity.PmsSpuEntity;
import com.atguigu.gmall.pms.service.PmsSpuService;


@Service("pmsSpuService")
public class PmsSpuServiceImpl extends ServiceImpl<PmsSpuMapper, PmsSpuEntity> implements PmsSpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsSpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsSpuEntity>()
        );

        return new PageResultVo(page);
    }

}