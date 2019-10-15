package com.haizhi.graph.dc.inbound.service.impl;

import com.google.common.base.Splitter;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.*;
import com.haizhi.graph.dc.core.dao.DcStoreDao;
import com.haizhi.graph.dc.core.model.po.*;
import com.haizhi.graph.dc.core.model.suo.DcTaskSuo;
import com.haizhi.graph.dc.core.model.vo.*;
import com.haizhi.graph.dc.inbound.dao.DcTaskDao;
import com.haizhi.graph.dc.inbound.dao.DcTaskInstanceDao;
import com.haizhi.graph.dc.core.model.qo.ApiInboundQo;
import com.haizhi.graph.dc.core.model.qo.BatchInboundQo;
import com.haizhi.graph.dc.core.model.qo.DcTaskQo;
import com.haizhi.graph.dc.core.model.qo.FlumeInboundQo;
import com.haizhi.graph.dc.inbound.service.DcTaskMetaService;
import com.haizhi.graph.dc.inbound.service.DcTaskService;
import com.haizhi.graph.dc.inbound.util.InboundUtil;
import com.haizhi.graph.sys.auth.dao.SysUserDao;
import com.haizhi.graph.sys.auth.model.po.SysUserPo;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import com.haizhi.graph.sys.file.model.vo.SysFileVo;
import com.haizhi.graph.sys.file.service.SysFileService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.haizhi.graph.dc.core.constant.TaskStatus.TASK_NOT_EXISTS;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Service
public class DcTaskServiceImpl extends JpaBase implements DcTaskService {

    @Autowired
    private DcTaskDao dcTaskDao;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private DcTaskInstanceDao dcTaskInstanceDao;

    @Autowired
    private DcTaskMetaService dcTaskMetaService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private DcStoreDao dcStoreDao;

    @Override
    @SuppressWarnings("unchecked")
    public PageResponse<DcTaskPageVo> findPage(DcTaskQo qo) {
        try {
            PageQo pageQo = qo.getPage();
            QDcTaskPo taskTable = QDcTaskPo.dcTaskPo;
            QDcTaskInstancePo instanceTable = QDcTaskInstancePo.dcTaskInstancePo;
            BooleanBuilder builder = new BooleanBuilder();
            String taskName = qo.getTaskName();
            if (StringUtils.isNotEmpty(qo.getGraph())) {
                builder.and(taskTable.graph.eq(qo.getGraph()));
            }
            if (StringUtils.isNoneEmpty(taskName)) {
                builder.and(taskTable.taskName.like(JQL.likeWrap(taskName)));
            }
            ExecutionType executionType = qo.getExecutionType();
            if (!Objects.isNull(executionType)) {
                builder.and(taskTable.executionType.eq(executionType));
            }
            TaskType taskType = qo.getTaskType();
            if (!Objects.isNull(taskType)) {
                builder.and(taskTable.taskType.eq(taskType));
            }
            TaskInstanceState taskInstanceState = qo.getTaskInstanceState();
            if (!Objects.isNull(taskInstanceState)) {
                if (taskInstanceState == TaskInstanceState.READY) {
                    builder.and(new BooleanBuilder().andAnyOf(instanceTable.state.eq(taskInstanceState), taskTable.lastInstanceId.eq(0L)));
                } else {
                    builder.and(instanceTable.state.eq(taskInstanceState));
                }
            }
            OperateType operateType = qo.getOperateType();
            if (!Objects.isNull(operateType)) {
                builder.and(taskTable.operateType.eq(operateType));
            }
            QueryResults<Tuple> results = jpa.select(taskTable, instanceTable)
                    .from(taskTable)
                    .leftJoin(instanceTable)
                    .on(taskTable.lastInstanceId.eq(instanceTable.id))
                    .where(builder)
                    .orderBy(taskTable.createdDt.desc())
                    .offset(pageQo.getPageSize() * (pageQo.getPageNo() - 1))
                    .limit(pageQo.getPageSize())
                    .fetchResults();
            long total = results.getTotal();
            List<DcTaskPageVo> voList = results.getResults()
                    .stream()
                    .map((Tuple tuple) ->
                    mapping(tuple)).collect(Collectors.toList());
            return PageResponse.success(voList, total, pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_PAGE_ERROR, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<List<DcTaskVo>> findTask(DcTaskQo qo) {
        try {
            QDcTaskPo taskTable = QDcTaskPo.dcTaskPo;
            BooleanBuilder builder = new BooleanBuilder();
            String taskName = qo.getTaskName();
            String graph = qo.getGraph();
            if (StringUtils.isNotEmpty(graph)) {
                builder.and(taskTable.graph.eq(graph));
            }
            String schema = qo.getSchema();
            if (StringUtils.isNotEmpty(schema)) {
                builder.and(taskTable.schema.eq(qo.getSchema()));
            }
            if (StringUtils.isNoneEmpty(taskName)) {
                builder.and(taskTable.taskName.like(JQL.likeWrap(taskName)));
            }
            ExecutionType executionType = qo.getExecutionType();
            if (!Objects.isNull(executionType)) {
                builder.and(taskTable.executionType.eq(executionType));
            }
            TaskType taskType = qo.getTaskType();
            if (!Objects.isNull(taskType)) {
                builder.and(taskTable.taskType.eq(taskType));
            }
            TaskState taskState = qo.getTaskState();
            if (!Objects.isNull(taskType)) {
                builder.and(taskTable.taskState.eq(taskState));
            }
            OperateType operateType = qo.getOperateType();
            if (!Objects.isNull(operateType)) {
                builder.and(taskTable.operateType.eq(operateType));
            }
            QueryResults<DcTaskPo> results = jpa.select(taskTable)
                    .from(taskTable)
                    .where(builder)
                    .orderBy(taskTable.createdDt.desc())
                    .fetchResults();
            List<DcTaskVo> voList = new ArrayList<>();
            List<DcTaskPo> poList = results.getResults();
            for (DcTaskPo po : poList) {
                voList.add(new DcTaskVo(po));
            }
            return Response.success(voList);
        } catch (Exception e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_NOT_FOUND, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<List<DcTaskInstanceVo>> findTaskInstance(DcTaskQo qo) {
        try {
            QDcTaskPo taskTable = QDcTaskPo.dcTaskPo;
            QDcTaskInstancePo instanceTable = QDcTaskInstancePo.dcTaskInstancePo;
            BooleanBuilder builder = new BooleanBuilder();
            Long taskId = qo.getTaskId();
            if (Objects.nonNull(taskId)) {
                builder.and(taskTable.id.eq(taskId));
            }
            if (StringUtils.isNotEmpty(qo.getGraph())) {
                builder.and(taskTable.graph.eq(qo.getGraph()));
            }
            String taskName = qo.getTaskName();
            if (StringUtils.isNoneEmpty(taskName)) {
                builder.and(taskTable.taskName.like(JQL.likeWrap(taskName)));
            }
            ExecutionType executionType = qo.getExecutionType();
            if (!Objects.isNull(executionType)) {
                builder.and(taskTable.executionType.eq(executionType));
            }
            TaskType taskType = qo.getTaskType();
            if (!Objects.isNull(taskType)) {
                builder.and(taskTable.taskType.eq(taskType));
            }
            TaskInstanceState taskInstanceState = qo.getTaskInstanceState();
            if (!Objects.isNull(taskInstanceState)) {
                if (taskInstanceState == TaskInstanceState.READY) {
                    builder.and(new BooleanBuilder().andAnyOf(instanceTable.state.eq(taskInstanceState), taskTable.lastInstanceId.eq(0L)));
                } else {
                    builder.and(instanceTable.state.eq(taskInstanceState));
                }
            }
            OperateType operateType = qo.getOperateType();
            if (!Objects.isNull(operateType)) {
                builder.and(taskTable.operateType.eq(operateType));
            }
            QueryResults<DcTaskInstancePo> results = jpa.select(instanceTable)
                    .from(taskTable)
                    .leftJoin(instanceTable)
                    .on(taskTable.lastInstanceId.eq(instanceTable.id))
                    .where(builder)
                    .orderBy(taskTable.createdDt.desc())
                    .fetchResults();
            List<DcTaskInstanceVo> voList = results.getResults().stream().map((DcTaskInstancePo po) -> DcTaskInstanceVo.create(po)).collect(Collectors.toList());
            return Response.success(voList);
        } catch (Exception e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_INSTANCE_NOT_FOUND, e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Long taskId = id;
            dcTaskDao.delete(taskId);
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            booleanBuilder.and(QDcTaskInstancePo.dcTaskInstancePo.taskId.eq(taskId));
            dcTaskInstanceDao.findAll(booleanBuilder).forEach(instancePo -> {
                dcTaskInstanceDao.delete(instancePo.getId());
            });
        } catch (Exception e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_DELETE_ERROR, e);
        }
    }

    @Override
    public DcTaskVo taskDetail(@NonNull Long taskId) {
        try {
            DcTaskPo dcTaskPo = dcTaskDao.findOne(taskId);
            if (Objects.isNull(dcTaskPo)) {
                throw new UnexpectedStatusException(TASK_NOT_EXISTS);
            }
            Long createById = Long.valueOf(dcTaskPo.getCreatedById());
            SysUserPo sysUserPo = sysUserDao.findOne(createById);
            DcTaskVo vo = new DcTaskVo(dcTaskPo);
            vo.setCreatedByName(sysUserPo.getName());
            vo.setTaskMetaVos(dcTaskMetaService.findByTaskId(taskId));
            Long lastInstanceId = dcTaskPo.getLastInstanceId();
            if (lastInstanceId == 0) {
                vo.setTaskDisplayState(TaskState.NORMAL.name());
            } else {
                vo.setTaskDisplayState(InboundUtil.getState(dcTaskInstanceDao.findOne(lastInstanceId).getState()));
            }
            if (dcTaskPo.getStoreId() != null) {
                DcStorePo dcStore = dcStoreDao.findOne(dcTaskPo.getStoreId());
                vo.setStoreName(dcStore == null ? "" : dcStore.getName());
            }
            if (vo.getTaskType() == TaskType.FILE && vo.getSourceType() == FileTaskSourceType.UPLOAD_FILE) {
                List<Long> ids = new ArrayList<>();
                String source = vo.getSource();
                if (StringUtils.isNotEmpty(source)) {
                    Splitter.on(",").split(vo.getSource()).forEach(str -> ids.add(Long.valueOf(str)));
                    Iterable<SysFilePo> poList = sysFileService.findByIds(ids.toArray(new Long[0]));
                    List<SysFileVo> fileVoList = new ArrayList<>();
                    if (Objects.nonNull(poList)) {
                        poList.forEach(sysFilePo -> fileVoList.add(new SysFileVo(sysFilePo)));
                    }
                    vo.setFilePoList(fileVoList);
                }
            }
            return vo;
        } catch (Exception e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_DETAIL_NOT_FOUND, e);
        }
    }

    @Override
    public DcTaskPo saveOrUpdate(DcTaskSuo dcTaskSuo) {
        DcTaskPo dcTaskPo = new DcTaskPo(dcTaskSuo);
        dcTaskPo = dcTaskDao.save(dcTaskPo);
        return dcTaskPo;
    }

    @Override
    public DcTaskPo findOne(@NonNull Long id) {
        return dcTaskDao.findOne(id);
    }

    @Override
    public DcTaskPo saveOrUpdate(DcTaskPo dcTaskPo) {
        return dcTaskDao.save(dcTaskPo);
    }

    @Override
    public PageResponse<ApiInboundVo> findTaskPage(ApiInboundQo apiInboundQo) {
        Long storeId = apiInboundQo.getStoreId();
        return getPageResult(storeId, TaskType.API, apiInboundQo.getPage(), instancePo -> new ApiInboundVo(instancePo));
    }

    @SuppressWarnings("unchecked")
    private <T> PageResponse<T> getPageResult(Long storeId, TaskType taskType, PageQo pageQo, Function<DcTaskInstancePo, T> function) {
        QDcTaskInstancePo table = QDcTaskInstancePo.dcTaskInstancePo;
        DcTaskPo dcTaskPo = dcTaskDao.findByStoreIdAndTaskType(storeId, taskType);
        if (Objects.isNull(dcTaskPo)) {
            return PageResponse.success(new ArrayList<>(), 0, pageQo);
        }
        Long taskId = dcTaskPo.getId();
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(table.taskId.eq(taskId));
        QueryResults<DcTaskInstancePo> results = jpa.select(table)
                .from(table)
                .where(builder)
                .orderBy(table.updatedDt.desc())
                .offset(pageQo.getPageSize() * (pageQo.getPageNo() - 1))
                .limit(pageQo.getPageSize())
                .fetchResults();
        List<T> apiInboundVoList = new ArrayList<>();
        results.getResults().forEach(instancePo -> {
            apiInboundVoList.add(function.apply(instancePo));
        });
        return PageResponse.success(apiInboundVoList, results.getTotal(), pageQo);
    }

    @Override
    public PageResponse<FlumeInboundVo> findTaskPage(FlumeInboundQo flumeInboundQo) {
        Long storeId = flumeInboundQo.getStoreId();
        return getPageResult(storeId, TaskType.API, flumeInboundQo.getPage(), instancePo -> new FlumeInboundVo(instancePo));
    }

    @Override
    public PageResponse<BatchInboundVo> findTaskPage(BatchInboundQo batchInboundQo) {
        Long storeId = batchInboundQo.getStoreId();
        return getPageResult(storeId, TaskType.API, batchInboundQo.getPage(), instancePo -> new BatchInboundVo(instancePo));
    }

    @Override
    public DcTaskPo createTaskIfNeeded(TaskType taskType, Long storeId) {
        DcTaskPo taskPo = dcTaskDao.findByStoreIdAndTaskType(storeId, taskType);
        if (Objects.isNull(taskPo)) {
            taskPo = new DcTaskPo();
            taskPo.setTaskType(taskType);
            taskPo.setStoreId(storeId);
            taskPo.setTaskName(taskType.name());
        } // TODO
        return taskPo;
    }

    @Override
    public List<DcTaskPo> findByStoreId(Long storeId) {
        return dcTaskDao.findByStoreId(storeId);
    }

    @Override
    public QueryResults<Tuple> findBySchema(Long schemaId) {
        QDcTaskPo dcTaskPo = QDcTaskPo.dcTaskPo;
        QDcSchemaPo dcSchemaPo = QDcSchemaPo.dcSchemaPo;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(dcSchemaPo.id.eq(schemaId));
        builder.and(dcSchemaPo.graph.eq(dcTaskPo.graph));
        QueryResults<Tuple> queryResults = jpa.select(dcTaskPo, dcSchemaPo)
                .from(dcTaskPo)
                .leftJoin(dcSchemaPo)
                .on(dcTaskPo.schema.eq(dcSchemaPo.schema))
                .where(builder)
                .limit(1L)
                .fetchResults();
        return queryResults;
    }

    @Override
    public Long countByStoreId(Long storeId) {
        return dcTaskDao.countByStoreId(storeId);
    }

    @Override
    public QueryResults<Tuple> findByGraph(Long graphId) {
        QDcTaskPo dcTaskPo = QDcTaskPo.dcTaskPo;
        QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(dcGraphPo.id.eq(graphId));
        QueryResults<Tuple> queryResults = jpa.select(dcTaskPo, dcGraphPo)
                .from(dcTaskPo)
                .leftJoin(dcGraphPo)
                .on(dcTaskPo.graph.eq(dcGraphPo.graph))
                .where(builder)
                .limit(1L)
                .fetchResults();
        return queryResults;
    }

    @Override
    public boolean checkRunByGraph(Long graphId) {
        QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
        QDcTaskPo dcTaskPo = QDcTaskPo.dcTaskPo;
        QDcTaskInstancePo dcTaskInstancePo = QDcTaskInstancePo.dcTaskInstancePo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(dcGraphPo.id.eq(graphId));
        builder.and(dcTaskInstancePo.state.in(TaskInstanceState.RUNNING, TaskInstanceState.READY, TaskInstanceState.EXPORTING));

        long count = jpa.select(dcTaskPo.id).from(dcTaskPo)
                .leftJoin(dcGraphPo).on(dcGraphPo.graph.eq(dcTaskPo.graph))
                .leftJoin(dcTaskInstancePo).on(dcTaskInstancePo.taskId.eq(dcTaskPo.id))
                .where(builder)
                .fetchCount();
        return count > 0;
    }

    @Override
    public boolean checkRunBySchemaId(Long schemaId) {
        QDcSchemaPo dcSchemaPo = QDcSchemaPo.dcSchemaPo;
        QDcTaskPo dcTaskPo = QDcTaskPo.dcTaskPo;
        QDcTaskInstancePo dcTaskInstancePo = QDcTaskInstancePo.dcTaskInstancePo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(dcSchemaPo.id.eq(schemaId));
        builder.and(dcSchemaPo.graph.eq(dcTaskPo.graph));
        builder.and(dcTaskInstancePo.state.in(TaskInstanceState.RUNNING, TaskInstanceState.READY, TaskInstanceState.EXPORTING));

        long count = jpa.select(dcTaskPo.id).from(dcTaskPo)
                .leftJoin(dcSchemaPo).on(dcSchemaPo.schema.eq(dcTaskPo.schema))
                .leftJoin(dcTaskInstancePo).on(dcTaskInstancePo.taskId.eq(dcTaskPo.id))
                .where(builder)
                .fetchCount();
        return count > 0;
    }

    @Override
    public Iterator<DcTaskPo> findSubmittedQuartzTask() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QDcTaskPo.dcTaskPo.taskState.in(TaskState.RUNNING, TaskState.TRIGGER));
        Iterator<DcTaskPo> ite = dcTaskDao.findAll(booleanBuilder).iterator();
        return ite;
    }

    ///////////////////////////////
    private static DcTaskPageVo mapping(Tuple tuple) {
        DcTaskPo dcTaskPo = tuple.get(0, DcTaskPo.class);
        DcTaskInstancePo dcTaskInstancePo = tuple.get(1, DcTaskInstancePo.class);
        DcTaskPageVo vo = new DcTaskPageVo(dcTaskPo);
        vo.setOperateType(dcTaskPo.getOperateType());
        vo.setErrorMode(dcTaskPo.getErrorMode());

        if (Objects.isNull(dcTaskInstancePo)){
            vo.setTaskDisplayState(InboundUtil.READY);
            return vo;
        }

        vo.setTotalRows(dcTaskInstancePo.getTotalRows());
        vo.setTotalSize(dcTaskInstancePo.getTotalSize());
        String lastExecuteDt = null;
        Date updateDt = dcTaskInstancePo.getUpdatedDt();
        if (Objects.nonNull(updateDt)) {
            lastExecuteDt = updateDt.toString();
        } else {
            Date createDt = dcTaskInstancePo.getCreatedDt();
            if (Objects.nonNull(createDt)) {
                lastExecuteDt = createDt.toString();
            }
        }
        vo.setLastExecuteDt(lastExecuteDt);
        vo.setTaskDisplayState(InboundUtil.getState(dcTaskInstancePo.getState()));
        return vo;
    }
}