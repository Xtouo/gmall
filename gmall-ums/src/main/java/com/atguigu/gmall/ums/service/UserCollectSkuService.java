package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.ums.api.entity.UserCollectSkuEntity;

/**
 * 关注商品表
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-19 20:25:26
 */
public interface UserCollectSkuService extends IService<UserCollectSkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

