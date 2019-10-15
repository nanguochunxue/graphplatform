package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import com.haizhi.graph.dc.core.model.qo.DcTaskInstanceQo;

import java.util.Map;

/**
 * Created by chengangxiong on 2019/02/11
 */
public interface DcTaskInstanceService {

    PageResponse findPage(DcTaskInstanceQo qo);

    DcTaskInstancePo findInstanceDetailByTaskId(Long id);

    DcTaskInstancePo saveOrUpdate(DcTaskInstancePo instancePo);

    DcTaskInstancePo create(Long taskId, OperateType operateType, Integer errorMode);

    DcTaskInstancePo findOne(Long id);

    void updateRowCount(Long taskInstanceId, Long rowCount);

    void updateAffectedRows(Long taskInstanceId, Map<StoreType, Long> storeTypeRowMap);

    DcTaskInstancePo findOrCreate(Long taskId, String dailyStr);
}
