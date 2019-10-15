package com.haizhi.graph.dc.inbound.task.quartz;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.common.redis.key.RKeys;
import com.haizhi.graph.common.rest.RestFactory;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.constant.DcErrorInfoField;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.common.monitor.MonitorService;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import com.haizhi.graph.dc.core.constant.*;
import com.haizhi.graph.dc.core.service.DcStoreParamService;
import com.haizhi.graph.dc.inbound.engine.JobRunner;
import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.inbound.service.DcTaskInstanceService;
import com.haizhi.graph.dc.inbound.service.DcTaskMetaService;
import com.haizhi.graph.dc.inbound.service.DcTaskService;
import com.haizhi.graph.dc.inbound.task.RecordServer;
import com.haizhi.graph.dc.inbound.task.conf.DcTask;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.sys.core.config.service.SysConfigService;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import com.haizhi.graph.sys.file.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.haizhi.graph.dc.core.constant.CoreConstant.*;
import static com.haizhi.graph.dc.core.constant.Constants.SYS_CONFIG_API_URL;

/**
 * Created by chengangxiong on 2019/01/31
 */
public class ScheduledTaskJob implements InterruptableJob {

    private static final GLog LOG = LogFactory.getLogger(ScheduledTaskJob.class);

    private static final String ID_PATTERN = "DC.EXTRACT=>{0}.task_{1}_{2}";

    private static final long GP_ETL_TIMEOUT = 10 * 60;   // seconds

    private Long taskId;

    private Long taskInstanceId;

    private JobRunner jobRunner;

    private String apiUrl;

    private Thread jobThread;

    private DcTaskService dcTaskService;

    private DcTaskInstanceService dcTaskInstanceService;

    private SysFileService sysFileService;

    private DcStoreParamService dcStoreParamService;

    private MonitorService monitorService;

    private DcTaskMetaService dcTaskMetaService;

    private StoreUsageService storeUsageService;

    private SysConfigService sysConfigService;

    private RestService restService = RestFactory.getRestService();

    private RedisService redisService;

    private RecordServer recordServer;

    private TaskRedisService taskRedisService;

    private Integer errorMode;

    private List<Map<String, Object>> totalRows = new LinkedList<>();

    private String graph;

    private String schema;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        prepareExecute(context);
        Date nextFireTime = context.getTrigger().getNextFireTime();
        DcTaskPo taskPo = dcTaskService.findOne(taskId);
        LOG.info("job type {0}, next fire time {1}", taskPo.getTaskType(), nextFireTime);
        try {
            LOG.info("time : " + System.currentTimeMillis() + " - task " + taskPo.getId());
            switch (taskPo.getTaskType()) {
                case FILE:
                    updateInstanceState(TaskInstanceState.RUNNING);
                    processFileTask(taskPo);
                    break;
                case GREEPLUM:
                    updateInstanceState(TaskInstanceState.EXPORTING);
                    processGpTask(taskPo);
                    break;
                default:
                    LOG.warn("not supported taskType:" + taskPo.getTaskType());
            }
            finishExecute();
        } catch (Exception ex) {
            exceptionExecute(ex);
        }
    }


    ///////////////////////
    // private functions
    ///////////////////////
    private void processGpTask(DcTaskPo taskPo) throws Exception {
        Long storeId = taskPo.getStoreId();
        if (Objects.isNull(storeId)) {
            throw new IllegalArgumentException("storeId is null");
        }
        String gpExportUrl = dcStoreParamService.findGPExportUrl(storeId);
        if (StringUtils.isNotEmpty(gpExportUrl)) {
            String realGPExportUrl = gpExportUrl;
            if (gpExportUrl.endsWith("/")) {
                realGPExportUrl = realGPExportUrl + "startExportFile";
            } else {
                realGPExportUrl = realGPExportUrl + "/startExportFile";
            }
            Map<String, Object> params = new HashMap<>();
            String sql = taskPo.getSource();
            extractParamFromSql(sql, params);
            params.put("taskInstanceId", taskInstanceId);
            Response resp = restService.doPost(realGPExportUrl, params, Response.class);
            String gpTraceKey = RKeys.DC_TASK_GRAPH_ETL_GP + ":" + taskInstanceId;
            long tryTraceSeconds = 0;
            do {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
                tryTraceSeconds += 3;
                boolean exists = redisService.hasKey(gpTraceKey);
                if (exists) {
                    break;
                }
            } while (tryTraceSeconds <= GP_ETL_TIMEOUT);
            Response response = redisService.get(gpTraceKey);
            LOG.info("call gp export response:\n{0}", JSON.toJSONString(response));
            Object data = response.getPayload().getData();
            if (((data instanceof Map) == false)) {
                throw new RuntimeException("gp etl response invalid format!");
            }
            Map<String, Object> retMap = Getter.getMap(data);
            if ((Integer) retMap.get(ExitCode) != 0) {
                throw new RuntimeException("perl script run fail!");
            }
            String exportOutputFile = String.valueOf(retMap.get(OutputFile));
            if (StringUtils.isBlank(exportOutputFile)) {
                throw new RuntimeException("gp etl export output file path is blank!");
            }
            boolean delSuccess = redisService.delete(gpTraceKey);
            LOG.info("delete redis gp trace key, success={0}, key={1}", delSuccess, gpTraceKey);
            updateInstanceState(TaskInstanceState.RUNNING);
            LOG.info("start read csv file from gp exporter ");
            runLocalServerFile(new File[]{new File(exportOutputFile)}, taskPo);
        }
    }

    private void extractParamFromSql(String sql, Map<String, Object> params) {
        int selectPos = StringUtils.indexOfIgnoreCase(sql, SELECT);
        int fromPos = StringUtils.indexOfIgnoreCase(sql, FROM);
        int wherePos = StringUtils.indexOfIgnoreCase(sql, WHERE);

        if (selectPos == -1 || fromPos == -1) {
            throw new RuntimeException("sql [" + sql + "] illegal");
        }
        String fields = sql.substring(selectPos + SELECT.length(), fromPos);
        String table = wherePos > -1 ? sql.substring(fromPos + FROM.length(), wherePos) : sql.substring(fromPos +
                FROM.length());
        String filter = wherePos > -1 ? sql.substring(wherePos + WHERE.length()) : "";

        LOG.info("fields :" + fields.trim());
        LOG.info("table :" + table.trim());
        LOG.info("filter :" + filter.trim());
        params.put("table", table.trim());
        params.put("fields", fields.trim());
        if (StringUtils.isNotBlank(filter.trim())) {
            params.put("filter", filter.trim());
        }
    }

    private void processFileTask(DcTaskPo taskPo) throws IOException {
        String sourceStr = taskPo.getSource();
        Map<String, String> sourceMap = JSON.parseObject(sourceStr, Map.class);
        String sourceKey = sourceMap.keySet().iterator().next();
        FileTaskSourceType sourceType = FileTaskSourceType.valueOf(sourceKey);

        if (sourceType == FileTaskSourceType.UPLOAD_FILE) {
            processUploadFile(taskPo, sourceMap, sourceType);
        } else if (sourceType == FileTaskSourceType.SERVER_PATH) {
            processServerFile(taskPo, sourceMap, sourceKey);
        }
    }

    private void processServerFile(DcTaskPo taskPo, Map<String, String> sourceMap, String sourceKey) throws IOException {
        String source = sourceMap.get(sourceKey);
        File file = new File(source);
        if (!file.exists()) {
            finishExecute();
        }
        if (file.isFile()) {
            runLocalServerFile(new File[]{file}, taskPo);
        } else {
            File[] subFile = file.listFiles(tmpFile -> {
                String path = tmpFile.getPath().toLowerCase();
                return tmpFile.isFile() && (path.endsWith(".csv") || path.endsWith(".json"));
            });
            runLocalServerFile(subFile, taskPo);
        }
    }

    private void processUploadFile(DcTaskPo taskPo, Map<String, String> sourceMap, FileTaskSourceType sourceType)
            throws IOException {
        Long totalSize;
        List<String> sourceArray;
        String source = sourceMap.get(sourceType.name());
        if (StringUtils.isNotEmpty(source)) {
            Iterable<String> idArray = Splitter.on(",").split(source);
            List<Long> idList = new ArrayList<>();
            for (String idStr : idArray) {
                idList.add(Long.parseLong(idStr));
            }
            Iterable<SysFilePo> filePos = sysFileService.findByIds(idList.toArray(new Long[0]));
            List<SysFilePo> sourceSysFilePo = Lists.newArrayList(filePos);
            totalSize = sourceSysFilePo.stream().map(SysFilePo::getFileSize).reduce((aLong, aLong2) -> aLong +
                    aLong2).get();
            sourceArray = sourceSysFilePo.stream().map(SysFilePo::getUrl).collect(Collectors.toList());
            if (taskPo.getTaskType() == TaskType.FILE && totalSize < 500 * 1024 * 1024L) {
                runLocalHDFSFile(sourceArray, taskPo, totalSize);
            } else {
                LOG.info("will not run task for total size bigger than 500M or not a file task");
                throw new IOException("will not run task for total size bigger than 500M or not a file task");
            }
        } else {
            updateInstanceState(TaskInstanceState.SUCCESS);
        }
    }

    private void runLocalServerFile(File[] subFile, DcTaskPo taskPo) throws IOException {
        List<Map<String, Object>> totalRows = new LinkedList<>();
        int readErrorCount = 0;
        List<DcTaskMetaPo> fieldMapping = findFieldMapping(taskId);
        long totalSize = 0;
        for (File file : subFile) {
            totalSize += file.length();
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                String line;
                List<String> allLine = new LinkedList<>();
                while ((line = reader.readLine()) != null) {
                    allLine.add(line);
                }
                readErrorCount += processLine(fieldMapping, totalRows, readErrorCount, file.getName(), allLine);
            }
        }
        if (totalSize > 0) {
            setInstanceSize(totalSize);
        }
        processTotalRows(totalRows, taskPo, readErrorCount);
    }

    private void processTotalRows(List<Map<String, Object>> totalRows, DcTaskPo taskPo, int readErrorCount) {
        DcTaskInstancePo taskInstance = dcTaskInstanceService.findOne(taskInstanceId);
        taskInstance.setTotalRows(totalRows.size() + readErrorCount);
        dcTaskInstanceService.saveOrUpdate(taskInstance);
        Consumer<Integer> errorRowsConsumer = integer -> {
            synchronized (taskInstanceId) {
                if (integer > 0) {
                    DcTaskInstancePo instance = dcTaskInstanceService.findOne(taskInstanceId);
                    instance.setErrorRows(instance.getErrorRows() + integer);
                    if (instance.getErrorRows().compareTo(instance.getTotalRows()) >= 0) {
                        instance.setState(TaskInstanceState.INTERRUPTED);
                    } else {
                        //increment error count
                        Long errorCount = taskRedisService.incrementErrorCount(taskInstanceId, integer);
                        if (Objects.nonNull(errorMode) && errorMode > 0) {
                            if (errorMode <= errorCount) {
                                instance.setState(TaskInstanceState.INTERRUPTED);
                            }
                        }
                    }
                    dcTaskInstanceService.saveOrUpdate(instance);
                } else {
                    boolean overErrorMode = taskRedisService.overErrorMode(taskInstanceId, errorMode);
                    if (overErrorMode) {
                        updateInstanceState(TaskInstanceState.INTERRUPTED);
                    }
                }
            }
        };
        if (readErrorCount > 0) {
            errorRowsConsumer.accept(readErrorCount);
        }
        this.sendData(taskPo.getGraph(), taskPo.getSchema(), totalRows, errorRowsConsumer);
    }


    @Override
    public void interrupt() throws UnableToInterruptJobException {
        LOG.info(" try to interrupt task {0}", taskId);
        jobThread.interrupt();
    }

    private void runOnSpark(List<String> sources) {
        DcTaskPo dcTaskPo = dcTaskService.findOne(taskId);
        updateInstanceState(TaskInstanceState.RUNNING);
        DcFlowTask task = new DcFlowTask();
        String idStr = MessageFormat.format(ID_PATTERN, dcTaskPo.getTaskType().name(), dcTaskPo.getId(),
                taskInstanceId);
        task.setId(idStr);
        task.setTaskType(dcTaskPo.getTaskType());
        task.setGraph(dcTaskPo.getGraph());
        task.setSchema(dcTaskPo.getSchema());
        task.setTaskId(dcTaskPo.getId());
        task.setInstanceId(taskInstanceId);
        task.setInboundApiUrl(apiUrl);
        task.setOperation(getOperation());
        task.setSource(sources);
        task.setDebugEnabled(false);
        task.setRunLocallyEnabled(true);
        jobRunner.waitForCompletion(task, info -> {
            String yarnStr = JSON.toJSONString(info);
            DcTaskInstancePo taskInstancePo = dcTaskInstanceService.findOne(taskInstanceId);
            taskInstancePo.setYarnAppInfo(yarnStr);
            dcTaskInstanceService.saveOrUpdate(taskInstancePo);
        });
    }

    private GOperation getOperation() {
        switch (dcTaskInstanceService.findOne(taskInstanceId).getOperateType()) {
            case INIT:
            case UPSERT:
                return GOperation.CREATE_OR_UPDATE;
            case DELETE:
                return GOperation.DELETE;
            default:
                return GOperation.CREATE_OR_UPDATE;
        }
    }

    private void runLocalHDFSFile(List<String> sources, DcTaskPo taskPo, Long totalSize) throws IOException {
        DcTaskInstancePo taskInstancePo = dcTaskInstanceService.findOne(taskInstanceId);
        taskInstancePo.setState(TaskInstanceState.RUNNING);
        taskInstancePo.setTotalSize(totalSize.intValue());
        dcTaskInstanceService.saveOrUpdate(taskInstancePo);
        int readErrorCount = 0;
        List<DcTaskMetaPo> fieldMapping = findFieldMapping(taskId);
        for (String source : sources) {
            try (FileReader fileReader = new FileReader(source); BufferedReader reader = new BufferedReader
                    (fileReader)) {
                List<String> allLine = new LinkedList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    allLine.add(line);
                }
                readErrorCount += processLine(fieldMapping, totalRows, readErrorCount, source, allLine);
            }
        }
        processTotalRows(totalRows, taskPo, readErrorCount);
    }

    private int processLine(List<DcTaskMetaPo> fieldMapping, List<Map<String, Object>> totalRows, int readErrorCount,
                            String source, List<String> allline) {
        if (source.toLowerCase().endsWith(".json")) {
            for (String line : allline) {
                try {
                    Map<String, Object> rowData = JSON.parseObject(line, Map.class);
                    Map<String, Object> dataMap = interceptByFieldMapping(rowData, fieldMapping);
                    if (!dataMap.isEmpty()) {
                        totalRows.add(dataMap);
                    }
                } catch (Exception e) {
                    readErrorCount++;
                    recordParseDataError(line, "can't parse to json : " + e.getMessage());
                }
            }
        } else if (source.toLowerCase().endsWith(".csv")) {
            String head = allline.get(0);
            if (!StringUtils.isEmpty(head)) {
                Iterable<String> headArray = Splitter.on(",").split(head);
                List<String> headList = Lists.newArrayList(headArray);
                for (int i = 1; i < allline.size(); i++) {
                    Iterable<String> lineData = Splitter.on(",").split(allline.get(i));
                    Map<String, Object> rowData = formatCSVLine(headList, lineData);
                    if (rowData != null) {
                        Map<String, Object> dataMap = interceptByFieldMapping(rowData, fieldMapping);
                        if (!dataMap.isEmpty()) {
                            totalRows.add(dataMap);
                        }
                    } else {
                        readErrorCount++;
                    }
                }
            }
            // TODO: 2019/6/5 parse .gp about greenPlumn database
        }
        return readErrorCount;
    }

    private Map<String, Object> formatCSVLine(List<String> headList, Iterable<String> lineData) {
        List<String> line = Lists.newArrayList(lineData);
        if (headList.size() == line.size()) {
            Map<String, Object> dataMap = new HashMap<>();
            for (int i = 0; i < headList.size(); i++) {
                dataMap.put(headList.get(i), line.get(i));
            }
            return dataMap;
        }
        return null;
    }


    private List<DcInboundDataSuo> createDcInboundDataSuoList(String graph, String schema, Long taskId, Long
            taskInstanceId, List<Map<String, Object>> totalRows) {
        List<DcInboundDataSuo> dcInboundDataSuoList = new LinkedList<>();
        List<Map<String, Object>> rows = null;
        for (Map<String, Object> row : totalRows) {
            if (rows == null) {
                rows = new LinkedList<>();
            }
            rows.add(row);
            if (rows.size() >= Constants.BATCH_SIZE) {
                DcInboundDataSuo suo = this.createDcInboundDataSuo(graph, schema, taskId, taskInstanceId, rows);
                dcInboundDataSuoList.add(suo);
                rows = null;
            }
        }
        if (rows != null) {
            DcInboundDataSuo suo = this.createDcInboundDataSuo(graph, schema, taskId, taskInstanceId, rows);
            dcInboundDataSuoList.add(suo);
        }
        return dcInboundDataSuoList;
    }

    private DcInboundDataSuo createDcInboundDataSuo(String graph, String schema, Long taskId, Long taskInstanceId,
                                                    List<Map<String, Object>> rows) {
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setOperation(getOperation());
        suo.setGraph(graph);
        suo.setSchema(schema);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, taskId);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, taskInstanceId);
        suo.setRows(rows);
        return suo;
    }

    private void sendData(String graph, String schema, List<Map<String, Object>> totalRows, Consumer<Integer>
            errorCountConsumer) {
        //segment data batch size
        List<DcInboundDataSuo> dcInboundDataSuoList = createDcInboundDataSuoList(graph, schema, this.taskId, this
                .taskInstanceId, totalRows);
        this.recordServer.sendRecords(dcInboundDataSuoList, errorCountConsumer);
    }

    private void sendData(List<Map<String, Object>> rows, DcTaskPo taskPo, Consumer<Integer> errorCountConsumer) {
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setOperation(getOperation());
        suo.setGraph(taskPo.getGraph());
        suo.setSchema(taskPo.getSchema());
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, taskPo.getId());
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, taskInstanceId);
        suo.setRows(rows);
        try {
            Response response = restService.doPost(apiUrl, suo, Response.class);
            LOG.info(response);
            if (!response.isSuccess()) {
                int size = (int) response.getPayload().getData();
                errorCountConsumer.accept(size);
            }
        } catch (Exception e) {
            LOG.error(e);
            errorCountConsumer.accept(rows.size());
        } finally {
            rows.clear();
        }
    }

    private void exceptionExecute(Exception ex) {
        LOG.error("quartz task execute exception", ex);
        DcTaskPo taskPo = dcTaskService.findOne(taskId);
        taskPo.setTaskState(TaskState.RUNNING);
        dcTaskService.saveOrUpdate(taskPo);

        DcTaskInstancePo taskInstancePo = dcTaskInstanceService.findOne(taskInstanceId);
        taskInstancePo.setState(TaskInstanceState.INTERRUPTED);
        dcTaskInstanceService.saveOrUpdate(taskInstancePo);

        //record error info
        DcInboundErrorInfo dcInboundErrorInfo = new DcInboundErrorInfo(taskPo.getGraph(), taskPo.getSchema(), DcInboundErrorInfo.ErrorType.CHECK_ERROR);
        dcInboundErrorInfo.setMsg(ex.getMessage());
        dcInboundErrorInfo.setTaskId(this.taskId);
        dcInboundErrorInfo.setTaskInstanceId(this.taskInstanceId);
        dcInboundErrorInfo.setRows(totalRows);
        monitorService.errorRecord(dcInboundErrorInfo);
    }

    private void finishExecute() {
        updateTaskState(TaskState.RUNNING);
    }

    private void prepareExecute(JobExecutionContext context) {
        jobThread = Thread.currentThread();
        DcTask dcTask = (DcTask) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_DCTASK);
        jobRunner = (JobRunner) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_JOB_RUNNER);
        monitorService = (MonitorService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_METRIC_SERVICE);
        DcTaskPo taskPo = dcTask.getDcTaskPo();
        taskId = taskPo.getId();
        graph = taskPo.getGraph();
        schema = taskPo.getSchema();
        dcTaskService = (DcTaskService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_TASK_SERVICE);
        dcTaskInstanceService = (DcTaskInstanceService) context.getMergedJobDataMap().get(InboundConstant
                .JOB_DATA_INSTANCE_SERVICE);
        sysFileService = (SysFileService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_FILE_SERVICE);
        dcStoreParamService = (DcStoreParamService) context.getMergedJobDataMap().get(InboundConstant
                .JOB_DATA_STORE_PARAM_SERVICE);
        dcTaskMetaService = (DcTaskMetaService) context.getMergedJobDataMap().get(InboundConstant
                .JOB_DATA_TASK_META_SERVICE);
        storeUsageService = (StoreUsageService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_STORE_USAGE);
        sysConfigService = (SysConfigService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_SYS_CONFIG);
        redisService = (RedisService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_REDIS_SERVICE);
        taskRedisService = (TaskRedisService) context.getMergedJobDataMap().get(InboundConstant.JOB_DATA_TASK_REDIS_SERVICE);
        errorMode = dcTask.getErrorMode();
        this.apiUrl = sysConfigService.getUrl(SYS_CONFIG_API_URL);
        DcTaskInstancePo taskInstancePo = dcTaskInstanceService.create(taskPo.getId(), dcTask.getOperateType(),
                errorMode);
        taskInstanceId = taskInstancePo.getId();
        taskPo.setLastInstanceId(taskInstancePo.getId());
        updateTaskState(taskPo, TaskState.TRIGGER);
        taskRedisService.setErrorMode(taskInstanceId, errorMode);
        recordServer = new RecordServer(apiUrl, taskRedisService, errorMode);
    }

    private void updateInstanceState(TaskInstanceState state) {
        DcTaskInstancePo taskInstancePo = dcTaskInstanceService.findOne(taskInstanceId);
        taskInstancePo.setState(state);
        dcTaskInstanceService.saveOrUpdate(taskInstancePo);
    }

    private void updateTaskState(DcTaskPo taskPo, TaskState state) {
        taskPo.setTaskState(state);
        dcTaskService.saveOrUpdate(taskPo);
    }

    private void updateTaskState(TaskState state) {
        DcTaskPo taskPo = dcTaskService.findOne(taskId);
        taskPo.setTaskState(state);
        dcTaskService.saveOrUpdate(taskPo);
    }

    private List<DcTaskMetaPo> findFieldMapping(Long taskId) {
        return dcTaskMetaService.findAllByTaskId(taskId);
    }

    private Map<String, Object> interceptByFieldMapping(Map<String, Object> rowData, List<DcTaskMetaPo> fieldMapping) {
        if (Objects.nonNull(fieldMapping) && !fieldMapping.isEmpty()) {
            Map<String, Object> afterModify = new HashMap<>();
            fieldMapping.stream().forEach(dcTaskMetaPo -> {
                switch (dcTaskMetaPo.getType()) {
                    case SEQ:
                        break;
                    case FIELD:
                        String srcField = dcTaskMetaPo.getSrcField();
                        String dstField = dcTaskMetaPo.getDstField();
                        if (rowData.containsKey(srcField)) {
                            Object srcValue = rowData.remove(srcField);
                            if (!StringUtils.isEmpty(dstField)) {
                                afterModify.put(dstField, srcValue);
                            }
                        }
                        break;
                    default:
                        throw new RuntimeException("not supported taskMetaType : " + dcTaskMetaPo.getType());
                }
            });
            return afterModify;
        }
        return rowData;
    }

    private void recordParseDataError(String line, String message) {
        DcInboundErrorInfo errorInfo = new DcInboundErrorInfo();
        errorInfo.setGraph(graph);
        errorInfo.setSchema(schema);
        errorInfo.setErrorType(DcInboundErrorInfo.ErrorType.CHECK_ERROR);
        errorInfo.setMsg(message);
        errorInfo.setTaskInstanceId(taskInstanceId);
        errorInfo.setRows(ImmutableList.of(ImmutableMap.of(DcErrorInfoField.NON_PARSE_DATA, line)));
        monitorService.errorRecord(errorInfo);
    }

    private void setInstanceSize(long totalSize) {
        try {
            DcTaskInstancePo taskInstancePo = dcTaskInstanceService.findOne(taskInstanceId);
            taskInstancePo.setTotalSize(Integer.parseInt(String.valueOf(totalSize)));
            dcTaskInstanceService.saveOrUpdate(taskInstancePo);
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}