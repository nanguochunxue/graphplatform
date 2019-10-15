package com.haizhi.graph.sys.auth.service;

import com.haizhi.graph.common.core.login.model.UserSysRoleVo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.po.SysUserPo;
import com.haizhi.graph.sys.auth.model.qo.*;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.model.uo.SysUserPwdUo;
import com.haizhi.graph.sys.auth.model.vo.*;
import com.haizhi.sys.sso.common.model.SSOLoginUser;

import java.util.List;
import java.util.Set;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
public interface SysUserService {

    Response<List<SysUserSimpleVo>> find(SysUserQo qo);

    Response<List<SysUserSimpleVo>> findUserList(SysUserIdsQo qo);

    PageResponse<SysUserVo> findPage(SysUserPageQo qo);

    UserSysRoleVo findUserRole(String userNo);

    Response<CheckExistVo> checkUserNo(SysCheckUserNoQo qo);

    Response saveOrUpdate(SysUserSuo suo);

    void register(SSOLoginUser ssoLoginUser);

    Response updatePassword(SysUserPwdUo uo);

    Response resetPassword(Long userId);

    Response delete(Long userId);

    Set<String> getUserResourceURL(Long userId);

    SysUserPo findOne(Long userId);

    LoginUserVo getLoginUserVo(Long userId);

    Response<List<SysResourceVo>> findUserResources(SysUserResourceQo qo);

    List<Long> getUserIdByRoleId(Long roleId);
}
