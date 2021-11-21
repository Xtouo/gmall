package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.ums.api.entity.UserEntity;

/**
 * 用户表
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-19 20:25:26
 */
public interface UserService extends IService<UserEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    Boolean toCheck(String data,Integer type);

    void register(UserEntity userEntity, String code);

    UserEntity login(String loginName, String password);

}

