package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.*;
import com.haizhi.graph.dc.core.model.vo.*;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.suo.ApiTaskSuo;
import com.haizhi.graph.dc.core.model.suo.BatchTaskSuo;
import com.haizhi.graph.dc.core.model.suo.DcTaskSuo;
import com.haizhi.graph.dc.core.model.suo.FlumeTaskSuo;
import com.haizhi.graph.sys.file.model.vo.SysFileVo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by chengangxiong on 2019/02/14
 */
public interface TaskInboundService {

    PageResponse findGraphs();

    PageResponse<DcTaskPageVo> findTaskPage(DcTaskQo dcTaskQo);

    Response<List<DcTaskVo>> findTask(DcTaskQo dcTaskQo);

    Response<List<DcTaskInstanceVo>> findTaskInstance(DcTaskQo dcTaskQo);

    Response<DcTaskVo> taskDetail(Long taskId);

    DcTaskInstanceVo findTaskInstanceDetail(Long taskInstanceId);

    TaskProcessVo findTotalProcess(Long taskId);

    PageResponse<DcTaskInstanceVo> findTaskInstanceHistoryPage(DcTaskInstanceQo qo);

    Response<BatchTaskProcessVo> findBatchTaskProcess(BatchTaskProcessQo batchTaskProcessQo);

    PageResponse<ApiInboundVo> findApiInboundPage(ApiInboundQo apiInboundQo);

    PageResponse<FlumeInboundVo> findFlumeInboundPage(FlumeInboundQo flumeInboundQo);

    PageResponse<BatchInboundVo> findBatchInboundPage(BatchInboundQo batchInboundQo);

    void createOrUpdate(ApiTaskSuo apiTaskSuo);

    void createOrUpdate(FlumeTaskSuo flumeTaskSuo);

    void createOrUpdate(BatchTaskSuo batchTaskSuo);

    void createOrUpdate(DcTaskSuo dcTaskSuo);

    void submit(Long taskId);

    void pause(Long taskId);

    void stop(Long taskId);

    void delete(Long taskId);

    void runOnce(TaskRunOnceQo onceQo);

    void resume(Long taskId);

    Response<CrontabDescVo> findCronDesc(CrontabDescQo qo);

    Response<List<TaskType>> findTaskTypeList();

    StreamingResponseBody download(Long id, HttpServletResponse response);

    Response<List<SysFileVo>> findTaskFiles(Long taskId);

    Response<List<DcTaskMetaVo>> findTaskMetas(Long taskId);

    Response<List<TaskProcessVo>> taskInstanceProcess(BatchTaskProcessQo qo);

    Response<Long> uploadFile(MultipartFile file);

    Response<List<String>> findSrcField(TaskSrcFieldQo fileId);

    Response<List<String>> findDstField(TaskDstFieldQo fileId);

    Response<String> findTaskScript(Long taskId);

    Response<DcNameCheckVo> checkServerPath(DcTaskServerPathCheckQo serverPath);

    PageResponse<List<TaskErrorPageVo>> findTaskErrorPage(TaskErrorQo taskErrorQo);

    Response<TaskErrorInfoVo> findTaskErrorInfo(TaskErrorInfoQo taskErrorInfoQo);

    Response checkStoreDelete(Long storeId);

    Response checkSchemaDelete(Long schemaId);

    Response checkGraphDelete(Long graphId);

    Response<TaskCheckVo> checkGraphUpdate(Long graphId);

    Response<TaskCheckVo> checkSchemaUpdate(Long schemaId);
}
