package com.haizhi.graph.sys.file.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import org.springframework.stereotype.Repository;

/**
 * Created by chengangxiong on 2019/01/31
 */
@Repository
public interface SysFileDao extends JpaRepo<SysFilePo> {
}
