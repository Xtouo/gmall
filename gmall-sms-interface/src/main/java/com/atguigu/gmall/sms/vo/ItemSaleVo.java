package com.atguigu.gmall.sms.vo;

import lombok.Data;

@Data
public class ItemSaleVo {

    // 营销类型：满减 打折 积分
    private String type;
    // 营销的文字描述
    private String desc;
}
