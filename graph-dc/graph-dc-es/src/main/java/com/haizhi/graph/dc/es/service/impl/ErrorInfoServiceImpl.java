package com.haizhi.graph.dc.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.LogicOperator;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.core.constant.DcErrorInfoField;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.core.dao.DcStoreDao;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.service.DcGraphStoreService;
import com.haizhi.graph.dc.core.service.DcSchemaService;
import com.haizhi.graph.dc.es.service.ErrorInfoService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.core.EsBuilder;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Create by zhoumingbing on 2019-05-17
 */
@Service
public class ErrorInfoServiceImpl implements ErrorInfoService {

    private static final GLog LOG = LogFactory.getLogger(ErrorInfoService.class);

    public static final String UNDER_LINE = "_";

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private EsIndexDao esIndexDao;

    @Autowired
    private DcSchemaService dcSchemaService;

    @Autowired
    private DcGraphStoreService dcGraphStoreService;

    @Autowired
    private EsSearchDao esSearchDao;

    @Autowired
    private DcStoreDao dcStoreDao;

    @Override
    public void recordInfo(List<ConsumerRecord<String, String>> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        records.stream().forEach(record -> recordInfo(record));
    }

    @Override
    public void recordInfo(ConsumerRecord<String, String> record) {
        DcInboundErrorInfo errorInfo = JSON.parseObject(record.value(), DcInboundErrorInfo.class);
        doRecord(errorInfo);
    }

    @Override
    public boolean doRecord(DcInboundErrorInfo errorInfo) {
        LOG.info("record error data , taskInstanceId={0} , data size={1} ,graph={2} "
                , errorInfo.getTaskInstanceId(), errorInfo.getRows().size(), errorInfo.getGraph());
        try {
            StoreURL storeURL = storeUsageService.findStoreURL(errorInfo.getGraph(), StoreType.ES);
            String graph = DcInboundErrorInfo.EsHelper.getEsGraph(errorInfo.getGraph(), storeURL);
            String type = DcInboundErrorInfo.EsHelper.getEsType(errorInfo.getSchema());
            String index = EsBuilder.getIndex(storeURL, graph, type);
            if (createIndexIfNotExist(storeURL, index)
                    && underLimit(storeURL, graph, type, errorInfo.getTaskInstanceId())) {
                Source source = getSource(errorInfo);
                if (ObjectUtils.allNotNull(source)) {
                    esIndexDao.bulkUpsert(storeURL, index, type, ImmutableList.of(source));
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    private Source getSource(DcInboundErrorInfo errorInfo) {
        String objectKey = getObjectKey(errorInfo.getRows());
        String affectedStore = getAffectedStore(errorInfo, STORE_NAME_SEPARATOR);
        if (StringUtils.isAnyBlank(objectKey, affectedStore)) {
            LOG.info("record error info ,can't get affectedStore , graph={0},schema={1}",
                    errorInfo.getGraph(), errorInfo.getSchema());
            return null;
        }
        String id = objectKey + UNDER_LINE + errorInfo.getTaskInstanceId() + UNDER_LINE + affectedStore;
        Source source = new Source();
        source.setId(id);
        source.addField(DcErrorInfoField.ID, id);
        source.addField(DcErrorInfoField.OBJECT_KEY, objectKey);
        source.addField(DcErrorInfoField.TASK_ID, errorInfo.getTaskId());
        source.addField(DcErrorInfoField.TASK_INSTANCE_ID, String.valueOf(errorInfo.getTaskInstanceId()));
        source.addField(DcErrorInfoField.ERROR_TYPE, errorInfo.getErrorType().name());
        source.addField(DcErrorInfoField.ERROR_LOG, errorInfo.getMsg());
        source.addField(DcErrorInfoField.AFFECTED_STORE, getAffectedStore(errorInfo, STORE_SEPARATOR));
        source.addField(DcErrorInfoField.DATA_ROW, JSON.toJSONString(errorInfo.getRows()));
        source.addField(DcErrorInfoField.UPDATED_DT, DateUtils.formatLocal(new Date()));
        return source;
    }

    private String getObjectKey(List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {
            String objectKey = Getter.get(Keys.OBJECT_KEY, row);
            if (StringUtils.isNotBlank(objectKey)) return objectKey;
        }
        return UUID.randomUUID().toString().replace(LINE_SYMBOL, EMPTY_STRING);
    }

    private static final String STORE_NAME_SEPARATOR = "#";
    private static final String STORE_SEPARATOR = ",";
    private static final String LINE_SYMBOL = "-";
    private static final String EMPTY_STRING = "";

    private String getAffectedStore(DcInboundErrorInfo errorInfo, String separator) {
        if (errorInfo.getErrorType() == DcInboundErrorInfo.ErrorType.RUNTIME_ERROR && Objects.nonNull(errorInfo.getStoreType())) {
            return errorInfo.getStoreType().getName();
        } else {
            DcSchemaPo dcSchemaPo = dcSchemaService.findByGraphAndSchema(errorInfo.getGraph(), errorInfo.getSchema());
            List<StoreType> storeTypes = new ArrayList<>();
            if (dcSchemaPo != null) {
                if (dcSchemaPo.isUseHBase()) {
                    storeTypes.add(StoreType.Hbase);
                }
                if (dcSchemaPo.isUseGdb()) {
                    storeTypes.add(StoreType.GDB);
                }
                if (dcSchemaPo.isUseSearch()) {
                    storeTypes.add(StoreType.ES);
                }
            }
            if (CollectionUtils.isEmpty(storeTypes)) return null;
            StringBuilder affectedStore = new StringBuilder();
            Iterator<StoreType> iterator = storeTypes.iterator();
            while (iterator.hasNext()) {
                affectedStore.append(iterator.next().getName());
                if (iterator.hasNext()) {
                    affectedStore.append(separator);
                }
            }
            return affectedStore.toString();
        }
    }

    private static final int ERROR_INFO_LIMIT = 10000;

    private boolean underLimit(StoreURL storeURL, String graph, String type, Long taskInstanceId) {
        EsQuery esQuery = new EsQuery();
        esQuery.setGraph(graph);
        esQuery.setSchemas(ImmutableSet.of(type));
        List<Map<String, Object>> rules = Lists.newArrayList();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("schema", type);
        filterMap.put("schemaType", SchemaType.VERTEX);
        filterMap.put(EKeys.field, DcErrorInfoField.TASK_INSTANCE_ID);
        filterMap.put("fieldType", "string");
        filterMap.put(EKeys.operator, "EQ");
        filterMap.put(EKeys.value, taskInstanceId);
        rules.add(filterMap);
        Map<String, Object> filter = new HashMap<>();
        filter.put("rules", rules);
        filter.put(EKeys.logicOperator, LogicOperator.AND.name());
        esQuery.setFilter(filter);
        EsQueryResult result = esSearchDao.search(storeURL, esQuery);
        if (result != null) {
            return result.getTotal() < ERROR_INFO_LIMIT;
        }
        return false;
    }

    private boolean createIndexIfNotExist(StoreURL storeURL, String index) {
        if (!esIndexDao.existsIndex(storeURL, index)) {
            LOG.info("will create index,index:[{0}] ", index);
            // create es index
            return esIndexDao.createIndex(storeURL, index);
        }
        return true;
    }
}
