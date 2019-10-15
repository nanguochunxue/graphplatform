package com.haizhi.graph.dc.inbound.service.impl;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import com.haizhi.graph.dc.core.constant.TaskStatus;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.core.model.po.QDcTaskInstancePo;
import com.haizhi.graph.dc.inbound.dao.DcTaskDao;
import com.haizhi.graph.dc.inbound.dao.DcTaskInstanceDao;
import com.haizhi.graph.dc.core.model.qo.DcTaskInstanceQo;
import com.haizhi.graph.dc.core.model.vo.DcTaskInstanceVo;
import com.haizhi.graph.dc.inbound.service.DcTaskInstanceService;
import com.querydsl.core.BooleanBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chengangxiong on 2019/02/11
 */
@Service
public class DcTaskInstanceServiceImpl extends JpaBase implements DcTaskInstanceService {

    @Autowired
    private DcTaskInstanceDao dcTaskInstanceDao;

    @Autowired
    private DcTaskDao dcTaskDao;

    @Override
    public PageResponse<DcTaskInstanceVo> findPage(DcTaskInstanceQo qo) {
        try {
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.CREATED_DT_DESC);
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            booleanBuilder.and(QDcTaskInstancePo.dcTaskInstancePo.taskId.eq(qo.getTaskId()));
            Page<DcTaskInstancePo> page = dcTaskInstanceDao.findAll(booleanBuilder, pageRequest);
            List<DcTaskInstanceVo> result = new ArrayList<>();
            page.forEach(po -> result.add(new DcTaskInstanceVo(po)));
            return PageResponse.success(result, page.getTotalElements(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(TaskStatus.TASK_INSTANCE_PAGE_ERROR, e);
        }
    }

    private DcTaskInstancePo getLastInstance(Long taskId) {
        DcTaskPo dcTaskPo = dcTaskDao.findOne(taskId);
        if (Objects.isNull(dcTaskPo)) {
            throw new UnexpectedStatusException(TaskStatus.TASK_NOT_EXISTS);
        }
        Long lastInstanceId = dcTaskPo.getLastInstanceId();
        if (lastInstanceId != null && lastInstanceId > 0) {
            DcTaskInstancePo result = dcTaskInstanceDao.findOne(lastInstanceId);
            return result;
        }
        return null;
    }

    @Override
    public DcTaskInstancePo findInstanceDetailByTaskId(@NonNull Long taskId) {
        DcTaskInstancePo instancePo = getLastInstance(taskId);
        return instancePo;
    }

    @Override
    public DcTaskInstancePo saveOrUpdate(DcTaskInstancePo instancePo) {
        return dcTaskInstanceDao.save(instancePo);
    }

    @Override
    public DcTaskInstancePo create(@NonNull Long taskId, OperateType operateType, Integer errorMode) {
        DcTaskInstancePo dcTaskInstancePo = new DcTaskInstancePo();
        dcTaskInstancePo.setTaskId(taskId);
        dcTaskInstancePo.setState(TaskInstanceState.READY);
        dcTaskInstancePo.setOperateType(operateType);
        dcTaskInstancePo.setErrorMode(errorMode);
        dcTaskInstancePo = dcTaskInstanceDao.save(dcTaskInstancePo);
        return dcTaskInstancePo;
    }

    @Override
    public DcTaskInstancePo findOne(@NonNull Long id) {
        return dcTaskInstanceDao.findOne(id);
    }

    @Override
    @Transactional
    public void updateRowCount(Long taskInstanceId, Long rowCount) {
        DcTaskInstancePo taskInstancePo = findOne(taskInstanceId);
        Long totalRows = taskInstancePo.getTotalRows() + rowCount;
        taskInstancePo.setTotalRows(totalRows.intValue());
        dcTaskInstanceDao.save(taskInstancePo);
    }

    @Override
    @Transactional
    public void updateAffectedRows(Long taskInstanceId, Map<StoreType, Long> storeTypeRowMap) {
        DcTaskInstancePo taskInstancePo = findOne(taskInstanceId);
        taskInstancePo.setHbaseAffectedRows(taskInstancePo.getHbaseAffectedRows() + getCount(storeTypeRowMap, StoreType.Hbase));
        taskInstancePo.setEsAffectedRows(taskInstancePo.getEsAffectedRows() + getCount(storeTypeRowMap, StoreType.ES));
        taskInstancePo.setGdbAffectedRows(taskInstancePo.getGdbAffectedRows() + getCount(storeTypeRowMap, StoreType.GDB));
        dcTaskInstanceDao.save(taskInstancePo);
    }

    private Integer getCount(Map<StoreType, Long> storeTypeRowMap, StoreType storeType) {
        return storeTypeRowMap.get(storeType) == null ? 0 : storeTypeRowMap.get(storeType).intValue();
    }

    @Override
    public DcTaskInstancePo findOrCreate(Long taskId, String dailyStr) {
        try {
            Date operateDt = new SimpleDateFormat("yyyy-MM-dd").parse(dailyStr);
            DcTaskInstancePo instance = dcTaskInstanceDao.findByTaskIdAndOperateDt(taskId, operateDt);
            if (Objects.isNull(instance)) {
                instance = new DcTaskInstancePo();
                instance.setTaskId(taskId);
                instance.setOperateDt(operateDt);
                instance.setOperateType(OperateType.UPSERT);
                instance.setState(TaskInstanceState.SUCCESS);
                instance = dcTaskInstanceDao.save(instance);
            }
            return instance;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
