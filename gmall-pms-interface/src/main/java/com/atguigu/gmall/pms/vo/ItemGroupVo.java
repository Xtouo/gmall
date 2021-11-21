package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.List;

@Data
public class ItemGroupVo {

    private Long id;

    private String name; // 分组名称

    private List<AttrValueVo> attrs;

}
