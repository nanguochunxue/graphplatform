package com.haizhi.graph.dc.inbound.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.LogicOperator;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.core.constant.DcErrorInfoField;
import com.haizhi.graph.dc.core.constant.ErrorType;
import com.haizhi.graph.dc.core.constant.TaskStatus;
import com.haizhi.graph.dc.core.model.qo.TaskErrorInfoQo;
import com.haizhi.graph.dc.core.model.qo.TaskErrorQo;
import com.haizhi.graph.dc.core.model.vo.TaskErrorInfoVo;
import com.haizhi.graph.dc.core.model.vo.TaskErrorPageVo;
import com.haizhi.graph.dc.inbound.service.TaskErrorService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.core.EsBuilder;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Create by zhoumingbing on 2019-05-11
 */
@Service
public class TaskErrorServiceImpl implements TaskErrorService {
    private static final GLog log = LogFactory.getLogger(TaskErrorServiceImpl.class);

    @Autowired
    private EsSearchDao esSearchDao;

    @Autowired
    private EsIndexDao esIndexDao;

    @Autowired
    private StoreUsageService storeUsageService;

    @Override
    public PageResponse<List<TaskErrorPageVo>> findPage(TaskErrorQo taskErrorQo) {
        StoreURL storeURL = storeUsageService.findStoreURL(taskErrorQo.getGraph(), StoreType.ES);
        String graph = DcInboundErrorInfo.EsHelper.getEsGraph(taskErrorQo.getGraph(), storeURL);
        String type = DcInboundErrorInfo.EsHelper.getEsType(taskErrorQo.getSchemaName());
        EsQuery esQuery = buildPageEsQuery(taskErrorQo, graph, type, storeURL);
        try {
            //ensure index exists before find
            if (esIndexDao.existsIndex(storeURL, EsBuilder.getIndex(storeURL, graph, type))) {
                EsQueryResult result = esSearchDao.search(storeURL, esQuery);
                //remove repeat result
                result = removeRepeatResult(result, taskErrorQo.getPage());
                return resolveResult(result, taskErrorQo.getPage());
            } else {
                log.info("index:[{0}] not exists ", esQuery.getGraph());
                return PageResponse.success(Lists.newArrayList(), 0L, taskErrorQo.getPage());
            }
        } catch (IndexNotFoundException e) {
            log.error("find error page ,not find index={0}",
                    DcInboundErrorInfo.EsHelper.getEsGraph(taskErrorQo.getGraph(), storeURL), e);
            return PageResponse.success(Lists.newArrayList(), 0L, taskErrorQo.getPage());
        } catch (ElasticsearchStatusException e2) {
            log.error("find error page ElasticsearchStatusException,not find index={0}"
                    , DcInboundErrorInfo.EsHelper.getEsGraph(taskErrorQo.getGraph(), storeURL), e2);
            return PageResponse.success(Lists.newArrayList(), 0L, taskErrorQo.getPage());
        } catch (Exception e) {
            log.error(e);
        }
        return PageResponse.error();
    }

    @Override
    public Response<TaskErrorInfoVo> findTaskErrorInfo(TaskErrorInfoQo dcErrorQo) {
        StoreURL storeURL = storeUsageService.findStoreURL(dcErrorQo.getGraph(), StoreType.ES);
        String graph = DcInboundErrorInfo.EsHelper.getEsGraph(dcErrorQo.getGraph(), storeURL);
        String type = DcInboundErrorInfo.EsHelper.getEsType(dcErrorQo.getSchema());
        EsQuery esQuery = buildDetailEsQuery(dcErrorQo, graph, type, storeURL);
        EsQueryResult result = esSearchDao.searchByIds(storeURL, esQuery);
        if (CollectionUtils.isEmpty(result.getRows())) {
            return Response.error(TaskStatus.TASK_ERROR_DETAIL_NOT_FOUND);
        }
        return buildTaskErrorInfoResponse(result.getRows().get(0));
    }

    /////////////////////////
    //////private function
    ////////////////////////

    private EsQuery buildPageEsQuery(TaskErrorQo taskErrorQo, String graph, String type, StoreURL storeURL) {
        EsQuery esQuery = new EsQuery();
        esQuery.setGraph(graph);
        esQuery.setSchemas(ImmutableSet.of(type));
        Map<String, Object> filter = getFilter(taskErrorQo, storeURL);
        if (filter.size() > 0) {
            esQuery.setFilter(filter);
        }
        List<Map<String, Object>> query = getQuery(taskErrorQo, storeURL);
        if (CollectionUtils.isNotEmpty(query)) {
            esQuery.setKeyword(taskErrorQo.getStoreType().name());
            esQuery.setQuery(query);
        }
        esQuery.setPageNo(taskErrorQo.getPage().getPageNo() - 1);
        esQuery.setPageSize(taskErrorQo.getPage().getPageSize());
        return esQuery;
    }

    private List<Map<String, Object>> getQuery(TaskErrorQo taskErrorQo, StoreURL storeURL) {
        List<Map<String, Object>> query = new ArrayList<>();
        if (ObjectUtils.allNotNull(taskErrorQo.getStoreType())) {
            query.add(getRule(taskErrorQo, OPERA_MATCH, DcErrorInfoField.AFFECTED_STORE,
                    taskErrorQo.getStoreType().name(), storeURL));
            query.get(0).put("boost", 3);
        }
        return query;
    }

    private PageResponse<List<TaskErrorPageVo>> resolveResult(EsQueryResult queryResult, PageQo pageQo) {
        if (queryResult == null || CollectionUtils.isEmpty(queryResult.getRows())) {
            return PageResponse.success(Lists.newArrayList(), 0L, pageQo);
        }
        List<TaskErrorPageVo> pageVos = new ArrayList<>();
        try {
            queryResult.getRows().stream().forEach(row -> {
                TaskErrorPageVo pageVo = new TaskErrorPageVo();
                pageVo.setErrorId(MapUtils.getString(row, DcErrorInfoField.ID));
                ErrorType errorType = null;
                if (StringUtils.isNotBlank(MapUtils.getString(row, DcErrorInfoField.ERROR_TYPE))) {
                    errorType = ErrorType.valueOf(MapUtils.getString(row, DcErrorInfoField.ERROR_TYPE));
                }
                pageVo.setErrorType(errorType);
                pageVo.setStoreTypes(MapUtils.getString(row, DcErrorInfoField.AFFECTED_STORE));
                pageVo.setUpdatedDt(MapUtils.getString(row, DcErrorInfoField.UPDATED_DT));
                pageVos.add(pageVo);
            });
        } catch (Exception e) {
            log.error("resolve inbound error result error", e);
        }
        return PageResponse.success(pageVos, queryResult.getTotal(), pageQo);
    }

    private EsQueryResult removeRepeatResult(EsQueryResult result, PageQo page) {
        long currentTotal = page.getPageSize() * page.getPageNo();
        if (page.getPageSize() < result.getTotal()
                && currentTotal > result.getTotal()
                && (currentTotal - result.getTotal()) < page.getPageSize()) {
            int fromIndex = new Long(currentTotal - result.getTotal() - 1).intValue();
            if (fromIndex < result.getRows().size() - 1) {
                result.setRows(result.getRows().subList(fromIndex, result.getRows().size() - 1));
            }
        }
        return result;
    }

    private Map<String, Object> getFilter(TaskErrorQo taskErrorQo, StoreURL storeURL) {
        List<Map<String, Object>> rules = Lists.newArrayList();
        rules.add(getRule(taskErrorQo, OPERA_EQ, DcErrorInfoField.TASK_INSTANCE_ID,
                String.valueOf(taskErrorQo.getTaskInstanceId()), storeURL));
        if (ObjectUtils.allNotNull(taskErrorQo.getErrorType())) {
            rules.add(getRule(taskErrorQo, OPERA_EQ, DcErrorInfoField.ERROR_TYPE + DcErrorInfoField.DOT_KEY_WORD,
                    taskErrorQo.getErrorType().name(), storeURL));
        }
        return getFilter(rules);
    }

    private Map<String, Object> getFilter(List<Map<String, Object>> rules) {
        Map<String, Object> filter = new HashMap<>();

        if (rules.size() > 0) {
            filter.put(EKeys.logicOperator, LogicOperator.AND.name());
        }
        if (CollectionUtils.isNotEmpty(rules)) {
            filter.put("rules", rules);
        }
        return filter;
    }

    private static final String OPERA_EQ = "EQ";
    private static final String OPERA_MATCH = "MATCH";

    private Map<String, Object> getRule(TaskErrorQo taskErrorQo, String operator, String field, String value, StoreURL storeURL) {
        String schema = DcInboundErrorInfo.EsHelper.getEsType(taskErrorQo.getSchemaName());
        return getRule(schema, operator, field, value);
    }

    private Map<String, Object> getRule(String schema, String operator, String field, String value) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("schema", schema);
        filterMap.put("schemaType", SchemaType.VERTEX);
        filterMap.put(EKeys.field, field);
        filterMap.put("fieldType", "string");
        filterMap.put(EKeys.operator, operator);
        filterMap.put(EKeys.value, value);
        return filterMap;
    }

    private EsQuery buildDetailEsQuery(TaskErrorInfoQo dcErrorQo, String graph, String type, StoreURL storeURL) {
        EsQuery esQuery = new EsQuery();
        esQuery.setGraph(graph);
        esQuery.setSchemas(ImmutableSet.of(type));
        esQuery.setSchemaKeys(getSchemaIds(dcErrorQo, storeURL));
        return esQuery;
    }

    private Map<String, Set<String>> getSchemaIds(TaskErrorInfoQo dcErrorQo, StoreURL storeURL) {
        Map<String, Set<String>> schemaIds = new HashMap<>();
        String esType = DcInboundErrorInfo.EsHelper.getEsType(dcErrorQo.getSchema());
        schemaIds.put(esType, ImmutableSet.of(dcErrorQo.getErrorId()));
        return schemaIds;
    }

    private Response<TaskErrorInfoVo> buildTaskErrorInfoResponse(Map<String, Object> row) {
        TaskErrorInfoVo vo = new TaskErrorInfoVo();
        vo.setDataRow(MapUtils.getString(row, DcErrorInfoField.DATA_ROW));
        vo.setErrorLog(MapUtils.getString(row, DcErrorInfoField.ERROR_LOG));

        return Response.success(vo);
    }


    private String buildErrorInfoDSL(TaskErrorQo taskErrorQo) {
        Map<String, Object> dslMap = new HashMap<>();
        Map<String, Object> conditionMap = new HashMap<>();
        dslMap.put("query", conditionMap);
        if (Objects.isNull(taskErrorQo.getStoreType()) && Objects.isNull(taskErrorQo.getErrorType())) {
            conditionMap.put("match_all", new HashMap<>());
        } else {
            List<Map<String, Map<String, String>>> shouldMap = new ArrayList<>();
            if (Objects.nonNull(taskErrorQo.getErrorType())) {
                Map<String, String> errorTypeCondition = new HashMap<>();
                errorTypeCondition.put(DcErrorInfoField.ERROR_TYPE, taskErrorQo.getErrorType().name());
                Map<String, Map<String, String>> matchMap = new HashMap<>();
                matchMap.put("match", errorTypeCondition);
                shouldMap.add(matchMap);
            }
            if (Objects.nonNull(taskErrorQo.getStoreType())) {
                Map<String, String> storeTypeMap = new HashMap<>();
                storeTypeMap.put(DcErrorInfoField.AFFECTED_STORE, taskErrorQo.getStoreType().name());
                Map<String, Map<String, String>> matchMap = new HashMap<>();
                matchMap.put("match", storeTypeMap);
                shouldMap.add(matchMap);
            }
            Map<String, Object> boolMap = new HashMap<>();
            boolMap.put("must", shouldMap);
            conditionMap.put("bool", boolMap);
        }

        dslMap.put("_source", ImmutableSet.of(DcErrorInfoField.ID,
                DcErrorInfoField.AFFECTED_STORE,
                DcErrorInfoField.ERROR_TYPE,
                DcErrorInfoField.UPDATED_DT));
        return JSON.toJSONString(dslMap);
    }

}
