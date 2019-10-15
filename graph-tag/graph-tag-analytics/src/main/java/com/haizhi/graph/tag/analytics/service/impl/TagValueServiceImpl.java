//package com.haizhi.graph.tag.analytics.service.impl;
//
//import com.haizhi.graph.common.bean.Result;
//import com.haizhi.graph.common.context.SpringContext;
//import com.haizhi.graph.common.json.JSONUtils;
//import com.haizhi.graph.common.key.Keys;
//import com.haizhi.graph.common.util.Getter;
//import com.haizhi.graph.engine.flow.tools.es.EsBulkLoader;
//import com.haizhi.graph.server.es.index.bean.Source;
//import com.haizhi.graph.server.es.search.vo.EsQuery;
//import com.haizhi.graph.server.es.search.vo.EsQueryResult;
//import com.haizhi.graph.server.es.search.vo.EsSearchDao;
//import com.haizhi.graph.tag.analytics.bean.TagResult;
//import com.haizhi.graph.tag.analytics.bean.TagValue;
//import com.haizhi.graph.tag.analytics.model.TagValueReq;
//import com.haizhi.graph.tag.analytics.service.TagValueService;
//import com.haizhi.graph.tag.analytics.util.Constants;
//import com.haizhi.graph.tag.analytics.util.TagUtils;
//import com.haizhi.graph.tag.core.domain.DataType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//
///**
// * Created by chengmo on 2018/7/27.
// */
//@Service
//public class TagValueServiceImpl implements TagValueService {
//
//    private static String SCHEMA_MAIN = SpringContext.getProperty(Constants.SCHEMA_MAIN);
//
//    @Autowired
//    private EsSearchDao esSearchDao;
//    @Autowired
//    private EsBulkLoader esBulkLoader;
//
//    @Override
//    public Result bulkUpsert(TagValueReq req) {
//        Result result = this.checkParameters(req);
//        if (!result.isSuccess()){
//            return result;
//        }
//        String graph = req.getGraph();
//        Long tagId = req.getTagId();
//
//        // getMatchesObjectKeys
//        Set<String> objectKeys = getMatchesObjectKeys(graph, req.getObjectKeys());
//
//        // build sources
//        List<Source> sourceList = new ArrayList<>();
//        for (String objectKey : objectKeys) {
//            sourceList.add(buildSource(tagId, objectKey));
//        }
//
//        // waitForCompletion
//        String indexName = TagUtils.getTagEsIndex(graph);
//        EsBulkLoader.EsBulkSource esBulkSource = new EsBulkLoader.EsBulkSource(indexName, TagValue._schema);
//        esBulkSource.setSources(sourceList);
//        esBulkLoader.addBulkSource(esBulkSource);
//        boolean success = esBulkLoader.waitForCompletion();
//        result.setSuccess(success);
//        return result;
//    }
//
//    ///////////////////////
//    // private functions
//    ///////////////////////
//    private Source buildSource(long tagId, String objectKey){
//        TagResult tagResult = new TagResult();
//        tagResult.setTagId(tagId);
//        tagResult.setObjectKey(objectKey);
//        tagResult.setDataType(DataType.BOOLEAN);
//        tagResult.setValue(1);
//        tagResult.setUpdateTime(new Date());
//
//        Source source = new Source(tagId + "#" + objectKey);
//        source.setSource(JSONUtils.toMap(tagResult));
//        return source;
//    }
//
//    private Set<String> getMatchesObjectKeys(String graph, Set<String> objectKeys){
//        EsQuery esQuery = new EsQuery(graph);
//        esQuery.setDebugEnabled(true);
//        esQuery.addType(SCHEMA_MAIN);
//        esQuery.setPageSize(5000);
//        esQuery.setIds(objectKeys);
//        esQuery.addHitFields(Keys.OBJECT_KEY, TagValue.value);
//
//        // searchByIds
//        EsQueryResult esResult = esSearchDao.searchByIds(esQuery);
//        if (!esResult.hasRows()) {
//            return Collections.emptySet();
//        }
//        Set<String> resultSet = new HashSet<>();
//        for (Map<String, Object> row : esResult.getRows()) {
//            String objectKey = Getter.get(Keys.OBJECT_KEY, row);
//            resultSet.add(objectKey);
//        }
//        return resultSet;
//    }
//
//    private Result checkParameters(TagValueReq req){
//        Result result = new Result();
//        if (Objects.isNull(req.getTagId()) || CollectionUtils.isEmpty(req.getObjectKeys())){
//            result.setSuccess(false);
//            result.setMessage("tagId is null or objectKeys is empty");
//            return result;
//        }
//        if (req.getObjectKeys().size() > 5000){
//            result.setSuccess(false);
//            result.setMessage("objectKeys size must not more than 5000");
//        }
//        return result;
//    }
//}
