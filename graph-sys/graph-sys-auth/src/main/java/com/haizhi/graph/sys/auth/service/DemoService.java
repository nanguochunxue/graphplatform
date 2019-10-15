package com.haizhi.graph.sys.auth.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.qo.SysUserPageQo;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.model.vo.SysUserSimpleVo;

import java.util.List;

/**
 * Created by chenmo on 2018/1/4.
 */
public interface DemoService {

    List<SysUserSimpleVo> find(SysUserPageQo qo);

    PageResponse<SysUserSimpleVo> findPage(SysUserPageQo qo);

    Response<SysUserSimpleVo> findUserRoles(SysUserPageQo qo);

    PageResponse<SysUserSimpleVo> findPageUserRoles(SysUserPageQo qo);

    Response saveOrUpdate(SysUserSuo suo);

    Response delete(Long userId);
}
