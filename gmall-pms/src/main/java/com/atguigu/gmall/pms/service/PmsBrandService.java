package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.PmsBrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-01 18:20:07
 */
public interface PmsBrandService extends IService<PmsBrandEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

