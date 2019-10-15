package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.TaskErrorInfoQo;
import com.haizhi.graph.dc.core.model.qo.TaskErrorQo;
import com.haizhi.graph.dc.core.model.vo.TaskErrorInfoVo;
import com.haizhi.graph.dc.core.model.vo.TaskErrorPageVo;

import java.util.List;

/**
 * Create by zhoumingbing on 2019-05-11
 */
public interface TaskErrorService {

    PageResponse<List<TaskErrorPageVo>> findPage(TaskErrorQo taskErrorQo);

    Response<TaskErrorInfoVo> findTaskErrorInfo(TaskErrorInfoQo dcErrorQo);
}
