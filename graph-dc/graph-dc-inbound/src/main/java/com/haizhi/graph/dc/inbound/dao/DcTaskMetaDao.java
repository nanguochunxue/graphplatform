package com.haizhi.graph.dc.inbound.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Repository
public interface DcTaskMetaDao extends JpaRepo<DcTaskMetaPo> {

    void deleteByTaskId(Long taskId);

    List<DcTaskMetaPo> findAllByTaskId(Long taskId);
}
