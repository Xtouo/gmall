package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.PmsCommentReplayMapper;
import com.atguigu.gmall.pms.entity.PmsCommentReplayEntity;
import com.atguigu.gmall.pms.service.PmsCommentReplayService;


@Service("pmsCommentReplayService")
public class PmsCommentReplayServiceImpl extends ServiceImpl<PmsCommentReplayMapper, PmsCommentReplayEntity> implements PmsCommentReplayService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PmsCommentReplayEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PmsCommentReplayEntity>()
        );

        return new PageResultVo(page);
    }

}