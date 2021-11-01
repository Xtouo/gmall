package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.PmsAttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-01 18:20:07
 */
public interface PmsAttrService extends IService<PmsAttrEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

