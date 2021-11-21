package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.sms.interfece.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
