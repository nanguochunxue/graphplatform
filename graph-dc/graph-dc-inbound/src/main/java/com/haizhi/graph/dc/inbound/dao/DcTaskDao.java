package com.haizhi.graph.dc.inbound.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Repository
public interface DcTaskDao extends JpaRepo<DcTaskPo> {

    DcTaskPo findByStoreIdAndTaskType(long storeId, TaskType taskType);

    List<DcTaskPo> findByStoreId(Long storeId);

    Long countByStoreId(Long storeId);
}
