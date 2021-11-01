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

import com.atguigu.gmall.pms.entity.PmsCommentEntity;
import com.atguigu.gmall.pms.service.PmsCommentService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品评价
 *
 * @author Xtouo
 * @email 172377058@qq.com
 * @date 2021-11-01 18:20:07
 */
@Api(tags = "商品评价 管理")
@RestController
@RequestMapping("pms/pmscomment")
public class PmsCommentController {

    @Autowired
    private PmsCommentService pmsCommentService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryPmsCommentByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = pmsCommentService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<PmsCommentEntity> queryPmsCommentById(@PathVariable("id") Long id){
		PmsCommentEntity pmsComment = pmsCommentService.getById(id);

        return ResponseVo.ok(pmsComment);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody PmsCommentEntity pmsComment){
		pmsCommentService.save(pmsComment);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody PmsCommentEntity pmsComment){
		pmsCommentService.updateById(pmsComment);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		pmsCommentService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
