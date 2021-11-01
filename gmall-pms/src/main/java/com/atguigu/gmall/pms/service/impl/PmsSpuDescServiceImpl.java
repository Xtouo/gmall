package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsSpuDescMapper;
import com.atguigu.gmall.pms.entity.PmsSpuDescEntity;
import com.atguigu.gmall.pms.service.PmsSpuDescService;


@Service("pmsSpuDescService")
public class PmsSpuDescServiceImpl extends ServiceImpl<PmsSpuDescMapper, PmsSpuDescEntity> implements PmsSpuDescService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsSpuDescEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsSpuDescEntity>()
        );

        return new PageResultVo(page);
    }

}