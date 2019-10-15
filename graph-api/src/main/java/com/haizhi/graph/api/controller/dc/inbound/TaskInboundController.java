package com.haizhi.graph.api.controller.dc.inbound;

import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.*;
import com.haizhi.graph.dc.core.model.vo.*;
import com.haizhi.graph.dc.core.redis.DcPubService;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import com.haizhi.graph.dc.core.service.DcGraphService;
import com.haizhi.graph.dc.core.constant.TaskErrorType;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.suo.DcTaskSuo;
import com.haizhi.graph.dc.inbound.service.TaskInboundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Api(description = "[数据接入-任务]-增删改查")
@RestController
@RequestMapping("/task")
public class TaskInboundController {

    @Autowired
    private TaskInboundService taskInboundService;

    @Autowired
    private DcPubService dcPubService;

    @Autowired
    private DcStorePubService dcStorePubService;

    @Autowired
    private DcGraphService dcGraphService;

    @ApiOperation(value = "新建、编辑任务")
    @PostMapping(value = "/save")
    public Response save(@RequestBody @Valid DcTaskSuo dcTaskSuo) {
        taskInboundService.createOrUpdate(dcTaskSuo);
        return Response.success();
    }

    @ApiOperation(value = "任务列表 - 分页查询 - 分页查询任务")
    @PostMapping(value = "/findTaskPage")
    public PageResponse<DcTaskPageVo> findTaskPage(@RequestBody @Valid DcTaskQo dcTaskQo) {
        return taskInboundService.findTaskPage(dcTaskQo);
    }

    @ApiOperation(value = "任务列表 - 用于调度任务脚本获取任务信息")
    @PostMapping(value = "/findTask")
    public Response<List<DcTaskVo>> findTask(@RequestBody @Valid DcTaskQo dcTaskQo) {
        return taskInboundService.findTask(dcTaskQo);
    }

    @ApiOperation(value = "任务实例列表 - 用于调度任务脚本获取任务实例")
    @PostMapping(value = "/findTaskInstance")
    public Response<List<DcTaskInstanceVo>> findTaskInstance(@RequestBody @Valid DcTaskQo dcTaskQo) {
        return taskInboundService.findTaskInstance(dcTaskQo);
    }

    @ApiOperation(value = "任务类型selector")
    @PostMapping(value = "/findTaskType")
    public Response<List<TaskType>> findTaskTypeList() {
        return taskInboundService.findTaskTypeList();
    }

    @ApiOperation(value = "任务列表 - 分页查询 - 任务进度查询")
    @PostMapping(value = "/findTaskProcess")
    public Response<BatchTaskProcessVo> findTaskProcess(@RequestBody @Valid BatchTaskProcessQo batchTaskProcessQo) {
        return taskInboundService.findBatchTaskProcess(batchTaskProcessQo);
    }

    @ApiOperation(value = "cron的详细描述")
    @PostMapping(value = "/findCronDescription")
    public Response<CrontabDescVo> findCronDescription(@RequestBody @Valid CrontabDescQo crontabDescQo) {
        return taskInboundService.findCronDesc(crontabDescQo);
    }

    @ApiOperation(value = "查询单个任务")
    @GetMapping(value = "/find")
    public Response<DcTaskVo> findOne(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return taskInboundService.taskDetail(id);
    }

    @ApiOperation(value = "删除任务")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        taskInboundService.delete(id);
        return Response.success();
    }

    @ApiOperation(value = "任务操作 - 定时任务 - 禁用")
    @GetMapping(value = "/pause")
    public Response pause(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        taskInboundService.pause(id);
        return Response.success();
    }

    @ApiOperation(value = "任务操作 - 定时任务 - 启用")
    @GetMapping(value = "/resume")
    public Response resume(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        taskInboundService.resume(id);
        return Response.success();
    }

    @ApiOperation(value = "任务操作 - 单次/定时任务 - 执行/立即执行")
    @PostMapping(value = "/runOnce")
    public Response runOnce(@RequestBody @Valid TaskRunOnceQo onceQo) {
        taskInboundService.runOnce(onceQo);
        return Response.success();
    }

    @ApiOperation(value = "任务操作 - 单次/定时任务 - 终止本次执行")
    @GetMapping(value = "/stop")
    public Response stop(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        taskInboundService.stop(id);
        return Response.success();
    }

    @ApiOperation(value = "执行单次任务")
    @GetMapping(value = "/submit")
    public Response submit(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        taskInboundService.submit(id);
        return Response.success();
    }

    @ApiOperation(value = "任务接入详情页 - 任务详情")
    @GetMapping(value = "/detail/task")
    public Response<DcTaskVo> taskDetail(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return taskInboundService.taskDetail(id);
    }

    @ApiOperation(value = "任务接入详情页 - 导入详情")
    @GetMapping(value = "/detail/instance")
    public Response<DcTaskInstanceVo> findTaskInstanceDetail(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        DcTaskInstanceVo vo = taskInboundService.findTaskInstanceDetail(id);
        return new Response(vo);
    }

    @ApiOperation(value = "任务接入详情页 - 导入详情 - 总进度")
    @GetMapping(value = "/detail/process")
    public Response<TaskProcessVo> process(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        TaskProcessVo processVo = taskInboundService.findTotalProcess(id);
        return new Response(processVo);
    }

    @ApiOperation(value = "任务接入详情页 - 分页查询导入历史")
    @PostMapping(value = "/detail/taskInstancePage")
    public PageResponse<DcTaskInstanceVo> findTaskInstanceHistoryPage(@RequestBody @Valid DcTaskInstanceQo qo) {
        return taskInboundService.findTaskInstanceHistoryPage(qo);
    }

    @ApiOperation(value = "任务接入详情页 - 分页查询导入历史 - 导入进度")
    @PostMapping(value = "/detail/taskInstanceProcess")
    public Response<List<TaskProcessVo>> taskInstanceProcess(@RequestBody @Valid BatchTaskProcessQo qo) {
        return taskInboundService.taskInstanceProcess(qo);
    }

    @ApiOperation(value = "文件任务 - 上传文件")
    @PostMapping(value = "/uploadTaskFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response<Long> uploadFile(@RequestParam(value = "file", required = true) MultipartFile file) {
        return taskInboundService.uploadFile(file);
    }

    @ApiOperation(value = "文件任务 - 删除文件")
    @DeleteMapping(value = "/deleteTaskFile")
    public Response deleteFile(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return Response.success();
    }

    @ApiOperation(value = "文件任务 - 下载文件")
    @GetMapping(value = "/downloadTaskFile")
    public StreamingResponseBody download(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id, HttpServletResponse response) {
        return taskInboundService.download(id, response);
    }

//    @ApiOperation(value = "文件任务 - 查询文件")
//    @GetMapping(value = "/findTaskFiles")
//    public Response<List<SysFileVo>> findTaskFiles(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
//        return taskInboundService.findTaskFiles(id);
//    }

    @ApiOperation(value = "任务 - 查询映射关系")
    @GetMapping(value = "/findTaskMetas")
    public Response<List<DcTaskMetaVo>> findTaskMetas(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return taskInboundService.findTaskMetas(id);
    }

    @ApiOperation(value = "任务新增、编辑 - 字段映射 - 源字段")
    @PostMapping(value = "/findSrcField")
    public Response<List<String>> findSrcField(@RequestBody @Valid TaskSrcFieldQo taskSrcFieldQo) {
        return taskInboundService.findSrcField(taskSrcFieldQo);
    }

    @ApiOperation(value = "任务新增、编辑 - 字段映射 - 目标字段")
    @PostMapping(value = "/findDstField")
    public Response<List<String>> findDstField(@RequestBody @Valid TaskDstFieldQo taskDstFieldQo) {
        return taskInboundService.findDstField(taskDstFieldQo);
    }

    @ApiOperation(value = "任务新增、编辑 - 服务器端输入文件路径校验")
    @PostMapping(value = "/checkServerPath")
    public Response<DcNameCheckVo> checkServerPath(@RequestBody @Valid DcTaskServerPathCheckQo serverPathCheckQo) {
        return taskInboundService.checkServerPath(serverPathCheckQo);
    }

    @ApiOperation(value = "任务执行 - 获取任务执行的shell脚本")
    @GetMapping(value = "/findTaskScript")
    public Response<String> findTaskScript(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return taskInboundService.findTaskScript(id);
    }

    @ApiOperation(value = "任务详情 - 错误详情 - 错误类型tab")
    @GetMapping(value = "/findErrorType")
    public Response<List<TaskErrorType>> findErrorType(@ApiParam(value = "任务实例ID，taskInstanceId", required = true) @RequestParam @Valid Long id) {
        return Response.success(EnumUtils.getEnumList(TaskErrorType.class));
    }

    @ApiOperation(value = "任务详情 - 错误详情 - 受影响的数据库tab")
    @GetMapping(value = "/findEffectedDB")
    public Response<List<StoreType>> findEffectedDB(@ApiParam(value = "任务实例ID，taskInstanceId", required = true) @RequestParam @Valid Long id) {
        return Response.success(Lists.newArrayList(StoreType.ES, StoreType.Hbase, StoreType.GDB));
    }

    @ApiOperation(value = "任务详情 - 错误详情 - table分页查询")
    @PostMapping(value = "/findTaskErrorPage")
    public PageResponse<List<TaskErrorPageVo>> findTaskErrorPage(@RequestBody @Valid TaskErrorQo taskErrorQo) {
        return taskInboundService.findTaskErrorPage(taskErrorQo);
    }

    @ApiOperation(value = "任务详情 - 错误详情 - 错误记录")
    @PostMapping(value = "/findTaskErrorInfo")
    public Response<TaskErrorInfoVo> findTaskErrorInfo(@RequestBody @Valid TaskErrorInfoQo taskErrorInfoQo) {
        return taskInboundService.findTaskErrorInfo(taskErrorInfoQo);
    }

    @ApiOperation(value = "验证是否可以删除数据源")
    @GetMapping(value = "/checkStoreDelete")
    public Response checkStoreDelete(Long storeId) {
        return taskInboundService.checkStoreDelete(storeId);
    }

    @ApiOperation(value = "验证是否可以删除图域名")
    @GetMapping(value = "/checkGraphDelete")
    public Response checkGraphDelete(Long graphId) {
        return taskInboundService.checkGraphDelete(graphId);
    }

    @ApiOperation(value = "验证是否可以删除表数据")
    @GetMapping(value = "/checkSchemaDelete")
    public Response checkSchemaDelete(Long schemaId) {
        return taskInboundService.checkSchemaDelete(schemaId);
    }

    @ApiOperation(value = "验证资源库是否可以修改")
    @GetMapping(value = "/checkGraphUpdate")
    public Response<TaskCheckVo> checkGraphUpdate(@ApiParam(value = "资源ID,graphId", required = true) @RequestParam @Valid Long graphId) {
        return taskInboundService.checkGraphUpdate(graphId);
    }

    @ApiOperation(value = "验证表或者表字段是否可以修改")
    @GetMapping(value = "/checkSchemaUpdate")
    public Response<TaskCheckVo> checkSchemaUpdate(@ApiParam(value = "表ID,schemaId", required = true) @RequestParam @Valid Long schemaId) {
        return taskInboundService.checkSchemaUpdate(schemaId);
    }

    @ApiOperation(value = "通过graph刷新 数据源缓存")
    @GetMapping(value = "/refreshByGraph")
    public Response refreshByGraph(@ApiParam(value = "graph 名称", required = true) @RequestParam @Valid String graph) {
        dcPubService.publish(graph);
        return Response.success();
    }

    @ApiOperation(value = "通过环境id刷新 DcStore缓存")
    @GetMapping(value = "/publishByEnvId")
    public Response refreshDcStoreByEnvId(@ApiParam(value = "环境id", required = true) @RequestParam @Valid Long envId) {
        dcStorePubService.publishByEnvId(envId);
        return Response.success();
    }

    @ApiOperation(value = "通过store id刷新 DcStore缓存")
    @GetMapping(value = "/refreshDcStoreByStoreId")
    public Response refreshDcStoreByStoreId(@ApiParam(value = "store id", required = true) @RequestParam @Valid Long storeId) {
        dcStorePubService.publishByStoreId(storeId);
        return Response.success();
    }

}
