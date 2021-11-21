package com.atguigu.gmall.auth.service;

import com.alibaba.nacos.client.utils.IPUtil;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.api.entity.UserEntity;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@EnableConfigurationProperties(JwtProperties.class)
@Service
public class AuthService {

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private JwtProperties jwtProperties;

    public void login(String loginName, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1.调用ums验证账号是否存在
        UserEntity userEntity = umsClient.login(loginName, password).getData();
        if (userEntity == null){
            //用户不存在
            throw new GmallException("用户不存在或账号密码错误！");
        }
        // 3.生成载荷 Map
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userEntity.getId());
        map.put("username", userEntity.getUsername());
        // 为了防止盗用，这里添加登录用户的ip地址
        map.put("ip", IpUtils.getIpAddressAtService(request));

        // 4.制作jwt类型的token
        String token = JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());

        // 5.放入cookie中
        CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), token, this.jwtProperties.getExpire() * 60);

        // 6.为了显示登录状态，把昵称放入cookie
        CookieUtils.setCookie(request, response, this.jwtProperties.getUnick(), userEntity.getNickname(), this.jwtProperties.getExpire() * 60);
    }
}
