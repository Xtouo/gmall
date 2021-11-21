package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
	private static final String pubKeyPath = "D:\\gmall-rsa\\rsa.pub";
    private static final String priKeyPath = "D:\\gmall-rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    //生成公钥私钥
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @BeforeEach
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
                this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5*60);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE2Mzc0OTQxMDV9.GlPDyjegerV2sE-Ua24SeKFCobJ4PEsrmb4aajnJBIgbRcwUH6sRGDfwn1JtJKJcalKe8yecBoiTLF0S-R47eT3Vj2WMRNhXZziMa8jsDd02oc07p5Mr6Qvxuf1XbVkbVTnQqCm7WmkmQdDud_0ul3UZ9KjxwomwcCwk7crrX0aTI3uE3WvDaTvyh7-eGF3-mdh8mmGamHfQ13NShMO0Q1dZ54CSosKdJ_wN0hqEeswJL7R0wXbpJUkREqYzfRXDzyd-n5rIOz60gnyBYGvj7DRKBQh_SetGFFyua-7qZLJfphE7AwB_VaX9zWrvsAQ9FFD-ahrGmVzkpjrc3Gv8yw";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}