package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.ums.api.entity.IntegrationHistoryEntity;

/**
 * 购物积分记录表
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-19 20:25:26
 */
public interface IntegrationHistoryService extends IService<IntegrationHistoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

