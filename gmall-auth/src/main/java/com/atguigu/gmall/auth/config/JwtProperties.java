package com.atguigu.gmall.auth.config;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String priFilePath;
    private String pubFilePath;
    private String secret;
    private Integer expire;
    private String cookieName;
    private String unick;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    //在构造器执行完执行该方法
    @PostConstruct
    public void init(){
        try {
            File pubFile = new File(pubFilePath);
            File priFile = new File(priFilePath);
            if (pubFile == null || priFile == null) {
                //如果公钥私钥为空 那么就创建公私钥
                RsaUtils.generateKey(pubFilePath, priFilePath, secret);
            }
            this.publicKey = RsaUtils.getPublicKey(pubFilePath);
            this.privateKey = RsaUtils.getPrivateKey(priFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
