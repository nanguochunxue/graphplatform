package com.haizhi.graph.dc.inbound.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.*;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.model.qo.*;
import com.haizhi.graph.dc.core.model.suo.*;
import com.haizhi.graph.dc.core.model.vo.*;
import com.haizhi.graph.dc.core.service.DcGraphService;
import com.haizhi.graph.dc.core.service.DcSchemaFieldService;
import com.haizhi.graph.dc.core.service.DcSchemaService;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.inbound.service.*;
import com.haizhi.graph.dc.inbound.task.TaskManager;
import com.haizhi.graph.dc.inbound.task.conf.DcTask;
import com.haizhi.graph.dc.inbound.task.quartz.ScheduledTaskJob;
import com.haizhi.graph.dc.inbound.util.CronUtil;
import com.haizhi.graph.dc.inbound.util.InboundUtil;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import com.haizhi.graph.sys.file.model.vo.SysFileVo;
import com.haizhi.graph.sys.file.service.SysFileService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.haizhi.graph.dc.core.constant.TaskStatus.*;

/**
 * Created by chengangxiong on 2019/02/14
 */
@Service
public class TaskInboundServiceImpl implements TaskInboundService {

    private static final GLog LOG = LogFactory.getLogger(ScheduledTaskJob.class);

    private static final Integer DEFAULT_ERROR_MODE = -1;

    @Autowired
    private DcGraphService dcGraphService;

    @Autowired
    private DcTaskService dcTaskService;

    @Autowired
    private DcTaskInstanceService dcTaskInstanceService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private DcTaskMetaService dcTaskMetaService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private DcSchemaFieldService dcSchemaFieldService;

    @Autowired
    private DcSchemaService dcSchemaService;

    @Autowired
    private TaskErrorService taskErrorService;

    // @PostConstruct // TODO
    public void postConstruct() {
        Iterator<DcTaskPo> runningTask = dcTaskService.findSubmittedQuartzTask();
        runningTask.forEachRemaining(dcTaskPo -> {
            Long lastInstanceId = dcTaskPo.getLastInstanceId();
            if (lastInstanceId != -1L) {
                DcTaskInstancePo dcTaskInstancePo = dcTaskInstanceService.findOne(lastInstanceId);
                OperateType operateType = dcTaskInstancePo.getOperateType();
                Preconditions.checkNotNull(operateType);
                if (dcTaskInstancePo.getState() == TaskInstanceState.READY && dcTaskPo.getExecutionType() == ExecutionType.ONCE) {
                    LOG.info("submit task{0}.{1} for startup", dcTaskPo.getId(), operateType);
                    doSubmit(dcTaskPo);
                } else if (dcTaskPo.getExecutionType() == ExecutionType.CRON) {
                    LOG.info("submit task{0}.{1} for startup", dcTaskPo.getId(), operateType);
                    doSubmit(dcTaskPo);
                } else {
                    LOG.info("not commit task {0}", dcTaskPo.getId());
                }
            }
        });
    }

    @Override
    public PageResponse findGraphs() {
        DcGraphQo dcGraphQo = new DcGraphQo();
        return dcGraphService.findPage(dcGraphQo);
    }

    @Override
    public PageResponse<DcTaskPageVo> findTaskPage(DcTaskQo dcTaskQo) {
        return dcTaskService.findPage(dcTaskQo);
    }

    @Override
    public Response<List<DcTaskVo>> findTask(DcTaskQo dcTaskQo) {
        return dcTaskService.findTask(dcTaskQo);
    }

    @Override
    public Response<List<DcTaskInstanceVo>> findTaskInstance(DcTaskQo dcTaskQo) {
        return dcTaskService.findTaskInstance(dcTaskQo);
    }

    @Override
    public Response<DcTaskVo> taskDetail(Long taskId) {
        checkTaskExists(taskId);
        DcTaskVo vo = dcTaskService.taskDetail(taskId);
        vo.setShellScript("curl 'http://host_port/api/task/submit?id=" + taskId + "'");
        return Response.success(vo);
    }

    @Override
    public Response<BatchTaskProcessVo> findBatchTaskProcess(BatchTaskProcessQo batchTaskProcessQo) {
        List<TaskProcessVo> processVoList = batchTaskProcessQo.getIds().stream().map(taskId -> findTotalProcess(taskId)).collect(Collectors.toList());
        return Response.success(new BatchTaskProcessVo(processVoList));
    }

    @Override
    public PageResponse<ApiInboundVo> findApiInboundPage(ApiInboundQo apiInboundQo) {
        return dcTaskService.findTaskPage(apiInboundQo);
    }

    @Override
    public PageResponse<FlumeInboundVo> findFlumeInboundPage(FlumeInboundQo flumeInboundQo) {
        return dcTaskService.findTaskPage(flumeInboundQo);
    }

    @Override
    public PageResponse<BatchInboundVo> findBatchInboundPage(BatchInboundQo batchInboundQo) {
        return dcTaskService.findTaskPage(batchInboundQo);
    }

    @Override
    public void createOrUpdate(ApiTaskSuo apiTaskSuo) {
        DcTaskPo dcTaskPo = new DcTaskPo(apiTaskSuo);
        DcTaskInstancePo instancePo = new DcTaskInstancePo();
        // TODO
        dcTaskService.saveOrUpdate(dcTaskPo);
    }

    @Override
    public void createOrUpdate(FlumeTaskSuo flumeTaskSuo) {
        DcTaskPo dcTaskPo = new DcTaskPo(flumeTaskSuo);
        dcTaskService.saveOrUpdate(dcTaskPo);
    }

    @Override
    public void createOrUpdate(BatchTaskSuo batchTaskSuo) {
        DcTaskPo dcTaskPo = new DcTaskPo(batchTaskSuo);
        dcTaskService.saveOrUpdate(dcTaskPo);
    }

    @Override
    public DcTaskInstanceVo findTaskInstanceDetail(Long taskId) {
        DcTaskInstancePo instancePo = dcTaskInstanceService.findInstanceDetailByTaskId(taskId);
        if (Objects.isNull(instancePo)) {
            return null;
        }
        return new DcTaskInstanceVo(instancePo);
    }

    @Override
    public TaskProcessVo findTotalProcess(Long taskId) {
        DcTaskInstancePo instancePo = dcTaskInstanceService.findInstanceDetailByTaskId(taskId);
        if (Objects.isNull(instancePo)) {
            instancePo = new DcTaskInstancePo();
        }
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        String displayState = InboundUtil.getState(instancePo.getState());
        DcSchemaPo dcSchema = dcSchemaService.findByGraphAndSchema(dcTaskPo.getGraph(), dcTaskPo.getSchema());
        if (Objects.nonNull(dcSchema)) {
            return new TaskProcessVo(instancePo, displayState, dcSchema.isUseGdb(), dcSchema.isUseSearch(), dcSchema.isUseHBase());
        } else {
            LOG.error("can`t find schema,taskId={0}", taskId);
            return new TaskProcessVo(instancePo, displayState, false, false, false);
        }
    }

    @Override
    public PageResponse<DcTaskInstanceVo> findTaskInstanceHistoryPage(DcTaskInstanceQo qo) {
        return dcTaskInstanceService.findPage(qo);
    }

    @Override
    public void submit(Long taskId) {
        try {
            DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
            if (Objects.isNull(dcTaskPo)) {
                throw new UnexpectedStatusException(TASK_NOT_EXISTS);
            }
            Long lastInstanceId = dcTaskPo.getLastInstanceId();
            if (lastInstanceId != 0) {
                DcTaskInstancePo instance = dcTaskInstanceService.findOne(lastInstanceId);
                if (instance != null && instance.getState() == TaskInstanceState.RUNNING) {
                    throw new UnexpectedStatusException(TASK_RUNNING_CANNOT_SUBMIT);
                }
            }
            doSubmit(dcTaskPo);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("submit task fail: {0}", e.getMessage());
            throw new UnexpectedStatusException("submit task fail:" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(@NonNull Long taskId) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
        Long lastInstanceId = dcTaskPo.getLastInstanceId();
        if (lastInstanceId != 0) {
            DcTaskInstancePo instance = dcTaskInstanceService.findOne(lastInstanceId);
            if (instance != null && instance.getState() == TaskInstanceState.RUNNING) {
                throw new UnexpectedStatusException(TASK_RUNNING_CANNOT_DELETE);
            }
        }
        dcTaskService.delete(taskId);
        try {
            taskManager.stop(taskId);
        } catch (SchedulerException e) {
            LOG.error("stop quartz job error", e);
            throw new UnexpectedStatusException("stop quartz job error", e);
        }
    }

    @Override
    public void runOnce(TaskRunOnceQo onceQo) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(onceQo.getId());
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
        Long lastInstanceId = dcTaskPo.getLastInstanceId();
        if (lastInstanceId != 0) {
            DcTaskInstancePo instance = dcTaskInstanceService.findOne(lastInstanceId);
            if (instance != null && (instance.getState() == TaskInstanceState.RUNNING)) {
                throw new UnexpectedStatusException(TASK_RUNNING_CANNOT_SUBMIT);
            }
        }
        DcTask dcTask = new DcTask(dcTaskPo);
        //reset error mode if necessary
        resetErrorModeIfNecessary(dcTask, onceQo);
        try {
            taskManager.runOnce(dcTask);
        } catch (SchedulerException e) {
            LOG.error("runOnce submit task error", e);
            throw new UnexpectedStatusException(TaskStatus.TASK_RUNONCE_ERROR);
        }
    }

    @Override
    public void resume(Long taskId) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
        try {
            taskManager.resume(new DcTask(dcTaskPo));
        } catch (SchedulerException e) {
            throw new UnexpectedStatusException(TaskStatus.CRON_EXPRESSION_ERROR);
        }
        dcTaskPo.setTaskState(TaskState.RUNNING);
        dcTaskService.saveOrUpdate(dcTaskPo);
    }

    @Override
    public Response<CrontabDescVo> findCronDesc(CrontabDescQo qo) {
        List<Date> timeList = CronUtil.findNextNumTime(qo.getCron(), qo.getNum());
        CrontabDescVo vo = new CrontabDescVo();
        vo.setCron(qo.getCron());
        vo.setNextExecuteTime(timeList);
        vo.setDescription(CronUtil.findDescription(qo.getCron()));
        return Response.success(vo);
    }

    @Override
    public Response<List<TaskType>> findTaskTypeList() {
        List<TaskType> taskTypes = Arrays.asList(TaskType.FILE, TaskType.HDFS, TaskType.HIVE);
        return Response.success(taskTypes);
    }

    @Override
    public StreamingResponseBody download(Long id, HttpServletResponse response) {
        SysFilePo sysFilePo = sysFileService.findById(id);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + sysFilePo.getName());
        return outputStream -> fileUploadService.download(sysFilePo, outputStream);
    }

    @Override
    public Response<List<SysFileVo>> findTaskFiles(Long taskId) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        String source = dcTaskPo.getSource();
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
        if (dcTaskPo.getTaskType() == TaskType.FILE) {
            try {
                String ids = (String) JSON.parseObject(dcTaskPo.getSource(), Map.class).get("UPLOAD_FILE");
                source = ids;
                return extractSource(source);
            } catch (Exception e) {
                return extractSource(source);
            }
        }
        return Response.success();
    }

    private Response<List<SysFileVo>> extractSource(String source) {
        List<Long> fileIds = Arrays.asList(source.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
        Iterable<SysFilePo> ite = sysFileService.findByIds(fileIds.toArray(new Long[0]));
        List<SysFileVo> voList = new ArrayList<>();
        ite.forEach(sysFilePo -> voList.add(new SysFileVo(sysFilePo)));
        return Response.success(voList);
    }

    @Override
    public Response<List<DcTaskMetaVo>> findTaskMetas(Long taskId) {
        List<DcTaskMetaVo> voList = dcTaskMetaService.findByTaskId(taskId);
        return Response.success(voList);
    }

    @Override
    public Response<List<TaskProcessVo>> taskInstanceProcess(BatchTaskProcessQo qo) {
        List<TaskProcessVo> voList = qo.getIds().stream().map(instanceId -> {
            DcTaskInstancePo instancePo = dcTaskInstanceService.findOne(instanceId);
            return instancePo;
        }).filter(Objects::nonNull).map(instancePo1 -> {
            Long taskId = instancePo1.getTaskId();
            DcTaskPo taskPo = dcTaskService.findOne(taskId);
            DcSchemaPo dcSchema = dcSchemaService.findByGraphAndSchema(taskPo.getGraph(), taskPo.getSchema());
            String displayState = InboundUtil.getState(instancePo1.getState());
            return new TaskProcessVo(instancePo1, displayState, dcSchema.isUseGdb(), dcSchema.isUseSearch(), dcSchema.isUseHBase());
        }).collect(Collectors.toList());
        return Response.success(voList);
    }

    @Override
    public Response<Long> uploadFile(MultipartFile file) {
        SysFilePo sysPo = fileUploadService.save(file);
        return Response.success(sysPo.getId());
    }

    @Override
    public Response<List<String>> findSrcField(TaskSrcFieldQo taskSrcFieldQo) {
        String serverPath = taskSrcFieldQo.getServerPath();
        File file = doCheckServerPath(serverPath);
        if (Objects.isNull(file)) {
            throw new UnexpectedStatusException(TASK_FILE_NOT_FOUND);
        }
        return readFileHeader(file);
    }

    private Response<List<String>> readFileHeader(File file) {
        String filePath = file.getPath();
        if (filePath.endsWith(".csv")) {
            return readCSVHeader(file);
        } else if (filePath.toLowerCase().endsWith(".json")) {
            return readJSONHeader(file);
        } else {
            return Response.error("not supported file type");
        }
    }

    private Response<List<String>> readJSONHeader(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (StringUtils.isEmpty(line)) {
                return Response.error("file first line is empty");
            }
            Map lineMap = JSON.parseObject(line, Map.class);
            List<String> srcField = new ArrayList<>(lineMap.keySet());
            return Response.success(srcField);
        } catch (IOException e) {
            return Response.error("file not found");
        }
    }

    private Response<List<String>> readCSVHeader(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (StringUtils.isEmpty(line)) {
                return Response.error("file first line is empty");
            }
            List<String> srcField = Splitter.on(",").splitToList(line);
            return Response.success(srcField);
        } catch (IOException e) {
            return Response.error("file not found");
        }
    }

    @Override
    public Response<List<String>> findDstField(TaskDstFieldQo taskDstFieldQo) {
        List<DcSchemaFieldPo> fieldPoList = dcSchemaFieldService.findByGraphAndSchema(taskDstFieldQo.getGraph(), taskDstFieldQo.getSchema());
        List<String> fieldList = fieldPoList.stream().map(dcSchemaFieldPo -> dcSchemaFieldPo.getField()).collect(Collectors.toList());
        return Response.success(fieldList);
    }

    @Override
    public Response<String> findTaskScript(Long taskId) {
        return Response.success("curl 'http://host_port/api/task/submit?id=" + taskId + "'");
    }

    @Override
    public Response<DcNameCheckVo> checkServerPath(DcTaskServerPathCheckQo serverPath) {
        try {
            doCheckServerPath(serverPath.getServerPath());
            return Response.success(new DcNameCheckVo());
        } catch (Exception e) {
            DcNameCheckVo vo = new DcNameCheckVo();
            vo.setCheckMsg(e.getMessage());
            return Response.success(vo);
        }
    }

    @Override
    public PageResponse<List<TaskErrorPageVo>> findTaskErrorPage(TaskErrorQo taskErrorQo) {
        return taskErrorService.findPage(taskErrorQo);
    }

    @Override
    public Response<TaskErrorInfoVo> findTaskErrorInfo(TaskErrorInfoQo dcErrorQo) {
        return taskErrorService.findTaskErrorInfo(dcErrorQo);
    }

    @Override
    public Response checkStoreDelete(Long storeId) {
        return checkDelete(dcTaskService.countByStoreId(storeId), StoreStatus.STORE_NOT_DELETE.getDesc());
    }

    @Override
    public Response checkSchemaDelete(Long schemaId) {
        QueryResults<Tuple> results = dcTaskService.findBySchema(schemaId);
        return checkDelete(results.getTotal(), CoreStatus.SCHEMA_CANNOT_DELETE_FOR_EXISTS_TASK.getDesc());
    }

    @Override
    public Response checkGraphDelete(Long graphId) {
        QueryResults<Tuple> results = dcTaskService.findByGraph(graphId);
        return checkDelete(results.getTotal(), CoreStatus.GRAPH_CANNOT_DELETE_FOR_EXISTS_TASK.getDesc());
    }

    @Override
    public Response<TaskCheckVo> checkGraphUpdate(Long graphId) {
        boolean result = dcTaskService.checkRunByGraph(graphId);
        return Response.success(new TaskCheckVo(!result));
    }

    @Override
    public Response<TaskCheckVo> checkSchemaUpdate(Long schemaId) {
        boolean result = dcTaskService.checkRunBySchemaId(schemaId);
        return Response.success(new TaskCheckVo(!result));
    }

    @Override
    @Transactional
    public void stop(@NonNull Long taskId) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
        DcTaskInstancePo instancePo = dcTaskInstanceService.findOne(dcTaskPo.getLastInstanceId());
        if (Objects.isNull(instancePo)) {
            throw new UnexpectedStatusException(TASK_INSTANCE_NOT_EXISTS);
        }
        if (!isRunning(instancePo.getState())) {
            throw new UnexpectedStatusException(TASK_INSTANCE_NOT_RUNNING);
        }
        //update task instance if necessary
        if (dcTaskPo.getLastInstanceId() == null) {
            return;
        }
        DcSchemaPo schemaPo = dcSchemaService.findByGraphAndSchema(dcTaskPo.getGraph(), dcTaskPo.getSchema());
        if (schemaPo == null) {
            return;
        }
        instancePo.setState(TaskInstanceState.INTERRUPTED);
        dcTaskInstanceService.saveOrUpdate(instancePo);
        dcTaskInstanceService.saveOrUpdate(instancePo);
        try {
            taskManager.stop(taskId);
        } catch (SchedulerException e) {
            throw new UnexpectedStatusException(TASK_STOP_ERROR);
        }
    }

    @Override
    public void pause(Long taskId) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
        if (dcTaskPo.getTaskState() != TaskState.RUNNING && dcTaskPo.getTaskState() != TaskState.TRIGGER) {
            throw new UnexpectedStatusException(TASK_NOT_RUNNING_CANNOT_PAUSE);
        }
        try {
            taskManager.pause(taskId);
        } catch (SchedulerException e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_PAUSE_ERROR);
        }
        dcTaskPo.setTaskState(TaskState.PAUSED);
        dcTaskService.saveOrUpdate(dcTaskPo);
    }

    private void doSubmit(DcTaskPo dcTaskPo) {
        try {
            taskManager.submit(new DcTask(dcTaskPo));
        } catch (SchedulerException e) {
            throw new UnexpectedStatusException("submit quartz job error", e);
        }
        dcTaskPo.setTaskState(TaskState.RUNNING);
        dcTaskService.saveOrUpdate(dcTaskPo);
    }

    @Override
    @Transactional
    public void createOrUpdate(DcTaskSuo dcTaskSuo) {
        if (dcTaskSuo.isRunImmediately()) {
            Preconditions.checkNotNull(dcTaskSuo.getOperateType(), new UnexpectedStatusException(TaskStatus.TASK_OPERATE_TYPE_NULL));
        }
        DcTaskPo dcTaskPo = new DcTaskPo(dcTaskSuo);
        dcTaskPo.checkTaskType();
        CronUtil.validCronExpression(dcTaskPo.getCron());
        dcTaskPo.setTaskState(TaskState.NORMAL);
        dcTaskPo.setLastInstanceId(0L);
        if (dcTaskPo.getTaskType() == TaskType.FILE) {
            Map<String, String> sourceMap = new HashMap<>();
            String inputSource = dcTaskSuo.getSource();
            FileTaskSourceType sourceType = dcTaskSuo.getSourceType();
            sourceMap.put(sourceType.name(), StringUtils.isEmpty(inputSource) ? Joiner.on(",").join(dcTaskSuo.getFileIds()) : dcTaskSuo.getSource());
            dcTaskPo.setSource(JSON.toJSONString(sourceMap));
            if (sourceType == FileTaskSourceType.SERVER_PATH) {
                doCheckServerPath(inputSource);
            }
        }
        DcTaskPo savedDcTaskPo = dcTaskService.saveOrUpdate(dcTaskPo);
        List<DcTaskMetaSuo> metaSuoList = dcTaskSuo.getTaskMetaSuoList();
        if (Objects.nonNull(metaSuoList) && !metaSuoList.isEmpty()) {
            dcTaskMetaService.save(dcTaskSuo.getTaskMetaSuoList(), savedDcTaskPo.getId());
        }
        if (dcTaskSuo.isRunImmediately()) {
            LOG.info(" create and run task:[{0}],errorMode:[{1}]", savedDcTaskPo.getId(), savedDcTaskPo.getErrorMode());
            runOnce(new TaskRunOnceQo(savedDcTaskPo.getId(), savedDcTaskPo.getErrorMode()));
        } else if (dcTaskPo.getExecutionType() == ExecutionType.CRON) {
            doSubmit(dcTaskPo);
        }
    }

    /////////////////////////
    //////private method///////
    /////////////////////////

    private void checkTaskExists(Long taskId) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TASK_NOT_EXISTS);
        }
    }

    private File doCheckServerPath(String inputSource) {
        if (StringUtils.isEmpty(inputSource)) {
            throw new UnexpectedStatusException(TaskStatus.FILE_PATH_NOT_EXISTS);
        }
        File serverFile = new File(inputSource);
        if (!serverFile.exists()) {
            throw new UnexpectedStatusException(TaskStatus.FILE_PATH_NOT_EXISTS);
        }
        if (serverFile.isFile()) {
            if (!InboundUtil.checkPathLegal(inputSource)) {
                throw new UnexpectedStatusException(TaskStatus.FILE_FORMAT_NOT_SUPPORT);
            }
            return serverFile;
        }
        if (serverFile.isDirectory()) {
            File[] illegalFiles = serverFile.listFiles(pathname -> pathname.isFile() && !InboundUtil.checkPathLegal(pathname.getPath()));
            if (illegalFiles.length != 0) {
                throw new UnexpectedStatusException(TASK_FILE_ILLEGAL, Joiner.on(",").join(illegalFiles));
            }
            File[] listFiles = serverFile.listFiles(pathname -> pathname.isFile() && InboundUtil.checkPathLegal(pathname.getPath()));
            if (listFiles.length == 0) {
                throw new UnexpectedStatusException(TASK_DIR_NOT_CONTAIN_FILE);
            }
            return listFiles[0];
        }
        return null;
    }

    private void resetErrorModeIfNecessary(DcTask dcTask, TaskRunOnceQo onceQo) {
        if (Objects.nonNull(onceQo.getErrorMode())) {
            dcTask.setErrorMode(onceQo.getErrorMode());
        }
        if (Objects.isNull(dcTask.getErrorMode())) {
            dcTask.setErrorMode(DEFAULT_ERROR_MODE);
        }
    }

    public static final String CAN_DELETE = "canDelete";

    private Response checkDelete(Long relevanceCount, String message) {
        Response response = Response.success(ImmutableMap.of(CAN_DELETE, relevanceCount == 0L));
        if (relevanceCount != 0L) {
            response.setMessage(message);
        }
        return response;
    }

    private boolean isRunning(TaskInstanceState instanceState) {
        return instanceState == TaskInstanceState.READY
                || instanceState == TaskInstanceState.EXPORTING
                || instanceState == TaskInstanceState.RUNNING;
    }
}
