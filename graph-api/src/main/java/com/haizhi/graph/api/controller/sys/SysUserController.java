package com.haizhi.graph.api.controller.sys;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.qo.SysCheckUserNoQo;
import com.haizhi.graph.sys.auth.model.qo.SysUserIdsQo;
import com.haizhi.graph.sys.auth.model.qo.SysUserPageQo;
import com.haizhi.graph.sys.auth.model.qo.SysUserQo;
import com.haizhi.graph.sys.auth.model.qo.SysUserResourceQo;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.model.uo.SysUserPwdUo;
import com.haizhi.graph.sys.auth.model.vo.CheckExistVo;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
import com.haizhi.graph.sys.auth.model.vo.SysUserSimpleVo;
import com.haizhi.graph.sys.auth.model.vo.SysUserVo;
import com.haizhi.graph.sys.auth.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by liulu on 2018/6/18.
 */
@Api(description = "[用户管理]")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "根据姓名、用户名等条件查询用户信息【GAP使用】")
    @PostMapping(value = "/find")
    public Response<List<SysUserSimpleVo>> find(@ApiParam(value = "请求参数", required = true) @RequestBody @Valid SysUserQo qo) {
        return sysUserService.find(qo);
    }

    @ApiOperation(value = "根据Id列表查询用户信息【GAP使用】")
    @PostMapping(value = "/findUserList")
    public Response<List<SysUserSimpleVo>> findUserList(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysUserIdsQo qo) {
        return sysUserService.findUserList(qo);
    }

    @ApiOperation(value = "根据用户信息获取系统权限【GAP使用】")
    @PostMapping(value = "/findUserResources")
    public Response<List<SysResourceVo>> findUserResources(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysUserResourceQo qo) {
        return sysUserService.findUserResources(qo);
    }

    @ApiOperation(value = "分页查询用户信息【WEB使用】")
    @PostMapping(value = "/findPage")
    public PageResponse<SysUserVo> findPage(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysUserPageQo qo) {
        return sysUserService.findPage(qo);
    }

    @ApiOperation(value = "检查用户名是否存在，全局唯一【WEB使用】")
    @PostMapping(value = "/checkUserNo")
    public Response<CheckExistVo> checkUserNo(@ApiParam(value = "用户名检查信息",
            required = true)@RequestBody @Valid SysCheckUserNoQo qo){
        return sysUserService.checkUserNo(qo);
    }

    @ApiOperation(value = "增加/编辑用户(系统创建)【WEB使用】")
    @PostMapping(value = "/saveOrUpdate")
    public Response save(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysUserSuo sysUserSuo) {
        return sysUserService.saveOrUpdate(sysUserSuo);
    }

    @ApiOperation(value = "修改用户密码【WEB使用】")
    @PostMapping(value = "/updatePassword")
    public Response updatePassword(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysUserPwdUo uo) {
        return sysUserService.updatePassword(uo);
    }

    @ApiOperation(value = "重置用户密码【WEB使用】")
    @PostMapping(value = "/resetPassword")
    public Response resetPassword(@ApiParam(value = "用户Id", required = true)@RequestParam @Valid Long userId) {
        return sysUserService.resetPassword(userId);
    }

    @ApiOperation(value = "删除用户【WEB使用】")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "用户Id", required = true)@RequestParam @Valid Long userId) {
        return sysUserService.delete(userId);
    }



}
