package com.haizhi.graph.api.controller.sys;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.sys.auth.model.qo.SysCheckRoleCodeQo;
import com.haizhi.graph.sys.auth.model.qo.SysCheckRoleNameQo;
import com.haizhi.graph.sys.auth.model.qo.SysRoleResourceQo;
import com.haizhi.graph.sys.auth.model.qo.SysRoleResourceTreeQo;
import com.haizhi.graph.sys.auth.model.suo.SysRoleSuo;
import com.haizhi.graph.sys.auth.model.vo.CheckExistVo;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
import com.haizhi.graph.sys.auth.model.vo.SysRoleListVo;
import com.haizhi.graph.sys.auth.model.vo.SysRoleResourceVo;
import com.haizhi.graph.sys.auth.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(description = "[角色管理]")
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "获取系统角色列表【WEB使用】")
    @GetMapping(value = "/findSysRoleList")
    public Response<SysRoleListVo> findSysRoleList() {
        return sysRoleService.findSysRoleList();
    }

    @ApiOperation(value = "获取系统角色的功能资源树(含未授权资源)【WEB使用】")
    @PostMapping(value = "/findRoleResourceTree")
    public Response<SysRoleResourceVo> findRoleResourceTree(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysRoleResourceTreeQo qo) {
        return sysRoleService.findRoleResourceTree(qo);
    }

    @ApiOperation(value = "检查系统角色名称是否存在，全局唯一【WEB使用】")
    @PostMapping(value = "/checkRoleName")
    public Response<CheckExistVo> checkRoleName(@ApiParam(value = "角色名称检查信息",
            required = true)@RequestBody @Valid SysCheckRoleNameQo qo){
        return sysRoleService.checkRoleName(qo);
    }

    @ApiOperation(value = "检查系统角色代码是否存在，全局唯一【WEB使用】")
    @PostMapping(value = "/checkRoleCode")
    public Response<CheckExistVo> checkRoleCode(@ApiParam(value = "角色代码检查信息",
            required = true)@RequestBody @Valid SysCheckRoleCodeQo qo){
        return sysRoleService.checkRoleCode(qo);
    }

    @ApiOperation(value = "保存或者修改角色【WEB使用】")
    @PostMapping(value = "/saveOrUpdate")
    public Response saveOrUpdate(@ApiParam(value = "请求参数", required = true)@RequestBody @Valid SysRoleSuo suo) {
        return sysRoleService.saveOrUpdate(suo);
    }

    @ApiOperation(value = "删除角色【WEB使用】")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "角色Id", required = true)@RequestParam @Valid Long roleId) {
        return sysRoleService.delete(roleId);
    }

    @ApiOperation(value = "根据角色信息查找已授权的功能资源列表【GAP使用】")
    @PostMapping(value = "/findRoleResources")
    public Response<List<SysResourceVo>> findRoleResources(@ApiParam(value = "请求对象", required = true)@RequestBody @Valid SysRoleResourceQo qo) {
        return sysRoleService.findRoleResources(qo);
    }

}
