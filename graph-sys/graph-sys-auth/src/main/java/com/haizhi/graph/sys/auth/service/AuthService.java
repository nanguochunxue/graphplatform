package com.haizhi.graph.sys.auth.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.vo.LoginUserVo;
import com.haizhi.graph.sys.auth.model.vo.LoginVo;
import com.haizhi.graph.sys.auth.shiro.model.LoginQo;

public interface AuthService {

    Response<LoginVo> login(LoginQo qo);

    Response logout();

    Response<LoginUserVo> findLoginUser();

    Response refreshUser(Long userId);

    Response isLogin();
}
