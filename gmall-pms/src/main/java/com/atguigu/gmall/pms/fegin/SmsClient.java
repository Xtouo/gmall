package com.atguigu.gmall.pms.fegin;

import com.atguigu.gmall.sms.interfece.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@FeignClient("sms-service")
@Service
public interface SmsClient extends GmallSmsApi {
}
