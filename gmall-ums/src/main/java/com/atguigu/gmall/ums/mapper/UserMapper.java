package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.api.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-19 20:25:26
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
