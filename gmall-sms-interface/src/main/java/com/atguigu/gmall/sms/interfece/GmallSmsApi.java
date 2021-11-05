package com.atguigu.gmall.sms.interfece;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GmallSmsApi {

    @PostMapping("sms/skubounds/saveSkuBounds")
    public ResponseVo saveSkuBounds(@RequestBody SkuSaleVo skuSaleVo);

}
