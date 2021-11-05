package com.atguigu.gmall.pms.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.BrandMapper;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, BrandEntity> implements BrandService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        String key = paramVo.getKey();
        if (StringUtils.isNotBlank(key)){
            queryWrapper.and(t -> t.eq("id",key).or().like("name",key));
        }
        IPage<BrandEntity> page = this.page(paramVo.getPage(),queryWrapper);
        return new PageResultVo(page);
    }
}