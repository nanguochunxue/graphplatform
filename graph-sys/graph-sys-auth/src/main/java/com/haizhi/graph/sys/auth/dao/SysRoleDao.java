package com.haizhi.graph.sys.auth.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.auth.model.po.SysRolePo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Repository
public interface SysRoleDao extends JpaRepo<SysRolePo> {

    List<SysRolePo> findByName(String name);

    List<SysRolePo> findByCode(String code);

    List<SysRolePo> findByCodeIn(Set<String> codes);

    @Modifying
    @Query("update SysRolePo s set s.code = :roleCode, s.updateById = :updateBy where s.id = :roleId ")
    void updateRoleCode(@Param("roleId")Long roleId, @Param("roleCode")String roleCode, @Param("updateBy")String updateBy);

}
