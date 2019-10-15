package com.haizhi.graph.sys.auth.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.auth.model.po.SysUserPo;
import org.springframework.stereotype.Repository;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Repository
public interface SysUserDao extends JpaRepo<SysUserPo> {

    SysUserPo findByUserNo(String userNo);
}
