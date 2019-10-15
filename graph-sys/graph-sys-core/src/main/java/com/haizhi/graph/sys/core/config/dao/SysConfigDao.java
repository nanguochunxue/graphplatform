package com.haizhi.graph.sys.core.config.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.core.config.model.SysConfigPo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chengmo on 2019/4/3.
 */
@Repository
public interface SysConfigDao extends JpaRepo<SysConfigPo> {

    List<SysConfigPo> findByTypeAndSubType(String type, String subType);

    List<SysConfigPo> findByType(String type);

    List<SysConfigPo> findByValue(String value);
}
