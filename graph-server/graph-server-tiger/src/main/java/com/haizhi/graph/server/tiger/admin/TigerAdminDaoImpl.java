package com.haizhi.graph.server.tiger.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import com.haizhi.graph.server.tiger.repository.TigerRepo;
import com.haizhi.graph.server.tiger.util.TigerWrapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.haizhi.graph.server.tiger.constant.TigerKeys.*;

/**
 * Created by tanghaiyang on 2019/3/5.
 */
@Service
public class TigerAdminDaoImpl implements GdbAdminDao {

    private static final GLog LOG = LogFactory.getLogger(TigerAdminDaoImpl.class);

    private static final String tpl = "tpl";

    private static LoadingCache<String, JSONObject> CACHE = CacheBuilder.newBuilder()
            .refreshAfterWrite(30, TimeUnit.SECONDS)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<String, JSONObject>() {
                @Override
                public JSONObject load(@Nullable String key) throws Exception {
                    return new JSONObject();
                }
            });

    @Autowired
    private TigerRepo tigerRepo;

    /**
     * url example:    http://192.168.1.234:9000/graph/work_graph
     * post empty json , test the api and check the response
     * */
    @Override
    public boolean existsDatabase(StoreURL storeURL, String database) {
        String graphSqlUrl = storeURL.getUrl();
        return existsSchema(database, graphSqlUrl, RESPONSE_JSON_GRAPHS, database);
    }

    /**
     * @see TigerAdminDaoImpl#existsDatabase
     * */
    @Override
    public boolean existsTable(StoreURL storeURL, String database, String table) {
        String graphSqlUrl = storeURL.getUrl();
        return existsSchema(database, graphSqlUrl, RESPONSE_JSON_VERTICES, table) ||
                existsSchema(database, graphSqlUrl, RESPONSE_JSON_EDGES, table);
    }

    /**
     * CREATE GRAPH work_graph ()
     * TODO: template store in mysql
     */
    @Override
    public boolean createDatabase(StoreURL storeURL, String database) {
        String graphSqlUrl = buildSqlUrl(storeURL.getUrl());
        String graphSql =  String.format("CREATE GRAPH %s()", database);
        try {
            JSONObject response = tigerRepo.execute(graphSqlUrl, graphSql);
            if(!response.getBooleanValue(RESPONSE_JSON_ERROR)){
//                buildQueryAll(database, url, tplDir);
                buildQueryAll(database, graphSqlUrl, tpl);
                return false;
            }else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean createTable(StoreURL storeURL, GdbSuo suo) {
        String graphSqlUrl = buildSqlUrl(storeURL.getUrl());
        String database = suo.getGraph();
        String table = suo.getSchema();
        SchemaType type = suo.getType();
        Map<String,FieldType> fields = suo.getFields();

        String graphSql = "";
        String fieldsStr = buildField(fields);

        String jobName = SCHEMA_CREATE_JOB_NAME + table;
        if(type.equals(SchemaType.VERTEX)){
            graphSql = String.format("USE GRAPH %s\nCREATE SCHEMA_CHANGE JOB %s FOR GRAPH %s {" +
                    "ADD %s %s (primary_id %s );}",
                    database, jobName, database, type, table, fieldsStr);
        }else if(type.equals(SchemaType.EDGE)){
            graphSql = String.format("USE GRAPH %s\nCREATE SCHEMA_CHANGE JOB %s FOR GRAPH %s {" +
                    "ADD UNDIRECTED %s %s (FROM *, TO *, %s);}",
                    database, jobName, database, type, table, fieldsStr);
        }
        try {
            JSONObject response = tigerRepo.execute(graphSqlUrl, graphSql);
            return !response.getBooleanValue(RESPONSE_JSON_ERROR);
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteTable(StoreURL storeURL, GdbSuo suo){
        String graphSqlUrl = buildSqlUrl(storeURL.getUrl());
        String database = suo.getGraph();
        String table = suo.getSchema();
        SchemaType type = suo.getType();
        String graphSql = String.format("USE GRAPH %s\nDROP %s %s", database, type, table);
        try {
            JSONObject response = tigerRepo.execute(graphSqlUrl, graphSql);
            return !response.getBooleanValue(RESPONSE_JSON_ERROR);
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public CudResponse bulkPersist(StoreURL storeURL, GdbSuo suo) {
        String importUrl = buildImportUrl(storeURL.getUrl());
        String graph = suo.getGraph();
        String schema = suo.getSchema();
        CudResponse cudResponse = new CudResponse(graph,schema);
        try {
            JSONObject tigerData = TigerWrapper.wrapBulk(suo);
            LOG.info(JSON.toJSONString(tigerData, SerializerFeature.PrettyFormat));
            JSONObject response = tigerRepo.executeUpsert(importUrl, tigerData);
            boolean isDeleted = !response.getBooleanValue(RESPONSE_JSON_ERROR);
            cudResponse.setRowsRead(suo.getRows().size());
            cudResponse.setSuccess(isDeleted);
            cudResponse.setMessage(response);
            return cudResponse;
        }catch (Exception e){
            cudResponse.setMessage(e.getMessage());
        }
        return cudResponse;
    }


    /**
    * curl -X DELETE "http://server_ip:9000/graph/{graph_name}/vertices/{vertex_type}[/{vertex_id}]?Timeout=30
    * */
    @Override
    public CudResponse delete(StoreURL storeURL, GdbSuo suo) {
        String importUrl = buildImportUrl(storeURL.getUrl());
        String graph = suo.getGraph();
        String schema = suo.getSchema();
        CudResponse cudResponse = new CudResponse(graph,schema);
        int rowsAffected = 0;
        int rowsErrors = 0;
        try {
            List<String> deleteUrls = TigerWrapper.wrapDelete(suo, importUrl);
            for (String deleteUrl : deleteUrls) {
                JSONObject response = tigerRepo.executeDelete(deleteUrl);
                if (!response.getBooleanValue(RESPONSE_JSON_ERROR)) {
                    rowsAffected++;
                } else {
                    rowsErrors++;
                }
            }
            cudResponse.setRowsRead(deleteUrls.size());
            cudResponse.setRowsAffected(rowsAffected);
            cudResponse.setRowsErrors(rowsErrors);
            if(rowsErrors>0) {
                cudResponse.setMessage("deleting with some failed!");
            }else {
                cudResponse.setMessage("deleted successful!");
            }
            return cudResponse;
        }catch (Exception e){
            cudResponse.setMessage(e.getMessage());
        }
        return cudResponse;
    }


    public boolean existVertex(String graphUrl, String database, String objectKey, String schema){
        String queryName = "existVertex";
        String queryUrl = graphUrl + "/" + database + "/" + queryName;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("objectKey", objectKey);
        parameters.put("schema", schema);
        JSONObject response = tigerRepo.executeQuery(queryUrl, parameters);
        LOG.info("response: \n{0}", response);
        return !response.getBooleanValue(RESPONSE_JSON_ERROR);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private String buildField(Map<String,FieldType> fields){
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = fields.keySet().iterator();
        for(;;){
            String filed = iterator.next();
            sb.append(filed);
            sb.append(" ");
            sb.append(buildType(fields.get(filed)));
            if (! iterator.hasNext()) break;
            sb.append(",");
        }

        return sb.toString();
    }

    private String buildType(FieldType fieldType){
        switch (fieldType){
            case LONG:     return SCHEMA_FIELD_TYPE_LONG;
            case DOUBLE:   return SCHEMA_FIELD_TYPE_DOUBLE;
            case DATETIME: return SCHEMA_FIELD_TYPE_DATETIME;
            case STRING:   return SCHEMA_FIELD_TYPE_STRING;
            default:       return SCHEMA_FIELD_TYPE_STRING;
        }
    }

    private void getSchema(String database, String graphSqlUrl){
        String graphSql = String.format("USE GRAPH %s\nls", database);
        JSONObject schema = tigerRepo.execute(graphSqlUrl, graphSql);
        CACHE.put(SCHEMA_CACHE_NAME, schema);
    }

    private boolean updateCacheKey(String key, String database, String graphSqlUrl){
        JSONObject value = CACHE.getIfPresent(key);
        if(value == null) {
            getSchema(database, graphSqlUrl);
            return true;
        }
        return false;
    }

    private boolean existsSchema(String database, String graphSqlUrl, String schemaType, String schemaName) {
        try {
            updateCacheKey(SCHEMA_CACHE_NAME, database, graphSqlUrl);
            JSONObject result = CACHE.getIfPresent(SCHEMA_CACHE_NAME);
            return result!=null && result.containsKey(schemaType) &&
                    result.getJSONArray(schemaType).contains(schemaName);
        }catch (Exception e){
            return false;
        }
    }

    // TODO: find queries template from mysql , mark at 2019/3/12 16:28
    private boolean buildQueryAll(String database, String graphSqlUrl, String queryDir){
        Map<String,String> queries = readTxtDir(queryDir);
        if(MapUtils.isNotEmpty(queries)) {
            Set<String> queryNames = queries.keySet();
            for (String queryName : queryNames) {
                String querySqlFile = queries.get(queryName);
                String querySql = String.format(querySqlFile, database, database);
                if(! existsQuery(database, queryName, graphSqlUrl)) {
                    JSONObject response = tigerRepo.execute(graphSqlUrl, querySql);
                    if(response.getBooleanValue(RESPONSE_JSON_ERROR)){
                        LOG.error("query build failed: {0}\n{1}", queryName, response);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static Map<String,String> readTxtDir(String fileDir) {
        Map<String,String> queries = new HashMap<>();
        String path = FileUtils.class.getResource("/" + fileDir).getPath();
        File rootDir = new File(path);
        File[] files = rootDir.listFiles();
        if(Objects.isNull(files)) return queries;
        for(File file: files){
            String queryName = file.getName();
            String querySql = FileUtils.readTxt(file.getAbsolutePath());
            queries.put(queryName, querySql);
        }
        return queries;
    }


    private boolean existsQuery( String database, String query, String graphSqlUrl) {
        return existsSchema(database, graphSqlUrl, RESPONSE_JSON_QUERIES, query);
    }

    // TODO: 2019/7/5
    private static String buildSqlUrl(String url){
        return "";
    }

    // TODO: 2019/7/5
    private static String buildImportUrl(String url){
        return "";
    }
}
