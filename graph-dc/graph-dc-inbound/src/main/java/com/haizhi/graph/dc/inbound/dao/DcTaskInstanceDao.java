package com.haizhi.graph.dc.inbound.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Repository
public interface DcTaskInstanceDao extends JpaRepo<DcTaskInstancePo> {

    Iterator<DcTaskInstancePo> findAllByTaskId(Long taskId);

    DcTaskInstancePo findByOperateDt(Date operateDt);

    DcTaskInstancePo findByTaskIdAndOperateDt(Long taskId, Date operateDt);
}
