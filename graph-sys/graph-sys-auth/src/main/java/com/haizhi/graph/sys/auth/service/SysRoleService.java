package com.haizhi.graph.sys.auth.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.qo.SysCheckRoleCodeQo;
import com.haizhi.graph.sys.auth.model.qo.SysCheckRoleNameQo;
import com.haizhi.graph.sys.auth.model.qo.SysRoleResourceQo;
import com.haizhi.graph.sys.auth.model.qo.SysRoleResourceTreeQo;
import com.haizhi.graph.sys.auth.model.suo.SysRoleSuo;
import com.haizhi.graph.sys.auth.model.uo.SysRoleResourceSuo;
import com.haizhi.graph.sys.auth.model.vo.CheckExistVo;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
import com.haizhi.graph.sys.auth.model.vo.SysRoleListVo;
import com.haizhi.graph.sys.auth.model.vo.SysRoleResourceVo;

import java.util.Collection;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
public interface SysRoleService {

    Response<SysRoleListVo> findSysRoleList();

    Response<SysRoleResourceVo> findRoleResourceTree(SysRoleResourceTreeQo qo);

    Response<CheckExistVo> checkRoleName(SysCheckRoleNameQo qo);

    Response<CheckExistVo> checkRoleCode(SysCheckRoleCodeQo qo);

    Response saveOrUpdate(SysRoleSuo suo);

    Response delete(Long roleId);

    Response<List<SysResourceVo>> findRoleResources(SysRoleResourceQo qo);

    List<String> findResourceByRoleIds(Collection<String> roleIds);
}
