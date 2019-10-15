package com.haizhi.graph.api.controller.auth;

import com.haizhi.graph.common.constant.GraphStatus;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.vo.LoginUserVo;
import com.haizhi.graph.sys.auth.model.vo.LoginVo;
import com.haizhi.graph.sys.auth.service.AuthService;
import com.haizhi.graph.sys.auth.shiro.model.LoginQo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by chengmo on 2018/05/22.
 */
@Api(description = "[权限]-登陆、退出、用户权限")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation(value = "用户登录")
    @PostMapping(value = "/login")
    public Response<LoginVo> login(@ApiParam(value = "登录参数", required = true) @RequestBody @Valid LoginQo qo) {
        return authService.login(qo);
    }

    @ApiOperation(value = "用户登出")
    @GetMapping(value = "/logout")
    public Response logout() {
        return authService.logout();
    }

    @ApiOperation(value = "获取登录用户信息（含权限信息）")
    @GetMapping(value = "/findLoginUser")
    public Response<LoginUserVo> findLoginUser() {
        return authService.findLoginUser();
    }

    @ApiOperation(value = "判断是否登录")
    @GetMapping(value = "/isLogin")
    public Response isLogin(){
        return authService.isLogin();
    }

    @ApiOperation("未授权警告")
    @RequestMapping(value = "/unauth", method = RequestMethod.GET)
    public Response unauth() {
        return Response.error(GraphStatus.UNAUTHORIZED);
    }

    @ApiOperation(value = "刷新用户缓存")
    @GetMapping(value = "/refreshUser")
    public Response refreshUser(@ApiParam(value = "用户ID", required = true) @Param(value = "userId") Long userId) {
        return authService.refreshUser(userId);
    }

}
