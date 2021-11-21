package com.atguigu.gmall.ums.api;


import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.ums.api.entity.UserEntity;
import org.springframework.web.bind.annotation.*;

public interface GmallUmsApi {

    @GetMapping("ums/user/check/{data}/{type}")
    public ResponseVo toCheck(@PathVariable String data, @PathVariable Integer type);

    @PostMapping("ums/user/register")
    public ResponseVo register(UserEntity userEntity, @RequestParam("code") String code);

    @GetMapping("ums/user/query")
    @ResponseBody
    public ResponseVo<UserEntity> login(@RequestParam("loginName") String loginName,
                            @RequestParam("password") String password);

}
