package com.haizhi.graph.sys.auth.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.auth.model.po.SysRoleResourcePo;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Repository
public interface SysRoleResourceDao extends JpaRepo<SysRoleResourcePo> {

    void deleteByRoleId(Long roleId);

    void deleteByRoleIdAndResourceIdIn(Long roleId, Set<Long> resourceIds);
}
