package com.atguigu.gmall.sms.interfece;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GmallSmsApi {

    @PostMapping("sms/skubounds/saveSkuBounds")
    public ResponseVo saveSkuBounds(@RequestBody SkuSaleVo skuSaleVo);

    @GetMapping("sms/skubounds/ItemSales/{skuId}")
    public ResponseVo<List<ItemSaleVo>> ItemSalesBySkuId(@PathVariable Long skuId);

}
