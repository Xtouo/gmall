package com.atguigu.gmall.pms.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.entity.PmsSpuEntity;
import com.atguigu.gmall.pms.service.PmsSpuService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * spu信息
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-01 18:20:07
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/pmsspu")
public class PmsSpuController {

    @Autowired
    private PmsSpuService pmsSpuService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryPmsSpuByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = pmsSpuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<PmsSpuEntity> queryPmsSpuById(@PathVariable("id") Long id){
		PmsSpuEntity pmsSpu = pmsSpuService.getById(id);

        return ResponseVo.ok(pmsSpu);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody PmsSpuEntity pmsSpu){
		pmsSpuService.save(pmsSpu);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody PmsSpuEntity pmsSpu){
		pmsSpuService.updateById(pmsSpu);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		pmsSpuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
