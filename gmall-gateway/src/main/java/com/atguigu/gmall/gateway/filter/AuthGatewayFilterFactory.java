package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.utils.IpUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {

    @Value("${jwt.cookieName}")
    private String cookieName;

    @Value("${jwt.pubFilePath}")
    private String pubFilePath;

    public AuthGatewayFilterFactory() {
        super(PathConfig.class);
    }

    @Data
    public static class PathConfig{
        private List<String> paths;
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("paths");
    }

    @Override
    public GatewayFilter apply(PathConfig config) {

        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                //1.判断当前请求是否在拦截名单中，不在则放行
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                String curPath = request.getURI().getPath();
                List<String> paths = config.paths;
                if (CollectionUtils.isEmpty(paths) || paths.stream().allMatch(path -> !curPath.startsWith(path))){
                    return chain.filter(exchange);
                }

                //2.从请求头和cookie中获取token
                String token = request.getHeaders().getFirst("token");
                if (StringUtils.isEmpty(token)){
                    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                    if (!CollectionUtils.isEmpty(cookies) && cookies.containsKey(cookieName)){
                        HttpCookie cookie = cookies.getFirst(cookieName);
                        token = cookie.getValue();
                    }
                }

                //3.判断token是否为空。为空重定向登录界面
                if (StringUtils.isEmpty(token)){
                    // 设置重定向的状态码和重定向的地址
//                    response.setStatusCode(HttpStatus.SEE_OTHER);
//                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
//                    return response.setComplete();
                    throw new GmallException("token为空");
                }

                try {
                    //4.使用公钥解析token 如果出现异常则重定向到登录界面
                    Map<String, Object> infoFromToken = JwtUtils.getInfoFromToken(token, RsaUtils.getPublicKey(pubFilePath));
                    //5.校验ip  载荷中获取
                    String ip = infoFromToken.get("ip").toString();
                    String requestIp = IpUtils.getIpAddressAtGateway(request);
                    if (!StringUtils.equals(ip,requestIp)){
                        throw new GmallException("ip不一致");
                    }

                    //6.把登录信息传给后续服务
                    request.mutate().header("userId",infoFromToken.get("userId").toString())
                            .header("username",infoFromToken.get("username").toString()).build();
                    exchange.mutate().request(request).build();

                    //7.放行
                    return chain.filter(exchange);

                } catch (Exception e) {
                    e.printStackTrace();
                    // 设置重定向的状态码和重定向的地址
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }
            }
        };
    }
}
