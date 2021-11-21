package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-11-01 15:29:22
 */
public interface SpuAttrValueService extends IService<SpuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    public void saveSpuAttr(SpuVo spuVo, Long spuId);

    List<SpuAttrValueEntity> getSpuValueBySkuIdAndCid(Long spuId, Long cid);


}

