package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.core.model.qo.ApiInboundQo;
import com.haizhi.graph.dc.core.model.qo.BatchInboundQo;
import com.haizhi.graph.dc.core.model.qo.DcTaskQo;
import com.haizhi.graph.dc.core.model.qo.FlumeInboundQo;
import com.haizhi.graph.dc.core.model.suo.DcTaskSuo;
import com.haizhi.graph.dc.core.model.vo.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;

import java.util.Iterator;
import java.util.List;

/**
 * Created by chengangxiong on 2019/01/29
 */
public interface DcTaskService {

    PageResponse<DcTaskPageVo> findPage(DcTaskQo qo);

    Response<List<DcTaskVo>> findTask(DcTaskQo qo);

    Response<List<DcTaskInstanceVo>> findTaskInstance(DcTaskQo qo);

    Iterator<DcTaskPo> findSubmittedQuartzTask();

    void delete(Long id);

    DcTaskVo taskDetail(Long taskId);

    DcTaskPo saveOrUpdate(DcTaskSuo dcTaskSuo);

    DcTaskPo findOne(Long id);

    DcTaskPo saveOrUpdate(DcTaskPo dcTaskPo);

    PageResponse<ApiInboundVo> findTaskPage(ApiInboundQo apiInboundQo);

    PageResponse<FlumeInboundVo> findTaskPage(FlumeInboundQo flumeInboundQo);

    PageResponse<BatchInboundVo> findTaskPage(BatchInboundQo batchInboundQo);

    DcTaskPo createTaskIfNeeded(TaskType inboundType, Long storeId);

    List<DcTaskPo> findByStoreId(Long storeId);

    QueryResults<Tuple> findBySchema(Long schemaId);

    Long countByStoreId(Long storeId);

    QueryResults<Tuple> findByGraph(Long graphId);

    boolean checkRunByGraph(Long graphId);

    boolean checkRunBySchemaId(Long schemaId);
}
