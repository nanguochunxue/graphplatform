package com.haizhi.graph.sys.auth.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.auth.model.po.SysResourcePo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Repository
public interface SysResourceDao extends JpaRepo<SysResourcePo> {

    List<SysResourcePo> findByTypeAndGroup(String type, String group);

    List<SysResourcePo> findByTypeInAndGroup(List<String> typeList, String group);

    List<SysResourcePo> findByTypeIn(List<String> types);
}
