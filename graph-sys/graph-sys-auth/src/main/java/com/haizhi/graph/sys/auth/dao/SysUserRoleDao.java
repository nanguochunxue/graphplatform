package com.haizhi.graph.sys.auth.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.auth.model.po.SysUserRolePo;
import org.springframework.stereotype.Repository;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Repository
public interface SysUserRoleDao extends JpaRepo<SysUserRolePo> {

    void deleteByUserId(Long userId);

    void deleteByRoleId(Long roleId);
}
