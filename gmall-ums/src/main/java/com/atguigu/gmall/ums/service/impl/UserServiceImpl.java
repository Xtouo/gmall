package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.api.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean toCheck(String data,Integer type) {
        // type 要校验的数据类型：1，用户名；2，手机；3，邮箱
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        switch (type){
            case 1: queryWrapper.eq("username",data);break;
            case 2: queryWrapper.eq("phone",data);break;
            case 3: queryWrapper.eq("email",data);break;
            default:
                return null;
        }
        return baseMapper.selectCount(queryWrapper) == 0;
    }

    @Override
    public void register(UserEntity userEntity, String code) {
        // TODO：1.校验验证码是否正确 redis   code

        // 2.生成盐
        String salt = StringUtils.substring(UUID.randomUUID().toString(), 0, 6);
        userEntity.setSalt(salt);

        // 3.加盐加密
        userEntity.setPassword(DigestUtils.md5Hex(userEntity.getPassword() + salt));

        // 4.新增用户
        userEntity.setLevelId(1l);
        userEntity.setNickname(userEntity.getUsername());
        userEntity.setSourceType(1);
        userEntity.setIntegration(1000);
        userEntity.setGrowth(1000);
        userEntity.setStatus(1);
        userEntity.setCreateTime(new Date());
        this.save(userEntity);

        // TODO: 删除redis中的验证码
    }

    @Override
    public UserEntity login(String loginName, String password) {
        // 1.根据登录名查询用户
        UserEntity userEntity = this.getOne(new QueryWrapper<UserEntity>()
                .eq("username", loginName)
                .or().eq("phone", loginName)
                .or().eq("email", loginName));

        // 2.如果查询的用户为null，则直接返回null
        if (userEntity == null){
            return userEntity;
        }

        // 3.对用户输入的明文（password）密码加盐加密
        password = DigestUtils.md5Hex(password + userEntity.getSalt());

        // 4.将加盐加密后的明文密码  和 数据库中的密码比较
        if (StringUtils.equals(password, userEntity.getPassword())){
            return userEntity;
        }
        return null;
    }

}