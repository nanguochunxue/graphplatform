package com.haizhi.graph.server.tiger.util;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import com.haizhi.graph.server.api.gdb.search.GQuery;
import com.haizhi.graph.server.tiger.query.impl.TigerQBuilderImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.haizhi.graph.server.tiger.constant.TigerKeys.IMPORTER_KEY_EDGES;
import static com.haizhi.graph.server.tiger.constant.TigerKeys.IMPORTER_KEY_VERTICES;

/**
 * Created by tanghaiyang on 2019/3/5.
 */
/*
* wrapper class
* */
public class TigerWrapper {

    private static final GLog LOG = LogFactory.getLogger(TigerWrapper.class);

    public static JSONObject wrapBulk(GdbSuo suo) {
        JSONObject ret = new JSONObject();
        String graph = suo.getGraph();
        String schema = suo.getSchema();
        SchemaType schemaType = suo.getType();
        List<Map<String, Object>> rows = suo.getRows();
        if(CollectionUtils.isEmpty(rows)){
            return ret;
        }
        if(StringUtils.isNoneEmpty(graph,schema)) {
            if (schemaType.equals(SchemaType.VERTEX)) {
                JSONObject vertices = putObject(ret, IMPORTER_KEY_VERTICES);
                JSONObject vertex = putObject(vertices, schema);
                rows.forEach(row->{
                    if (row.containsKey(Keys.OBJECT_KEY)) {
                        String key = (String) row.get(Keys.OBJECT_KEY);
                        JSONObject attributes = putObject(vertex, key);
                        row.remove(Keys.OBJECT_KEY);
                        putAttributes(row, attributes);
                    }
                });
            } else if (schemaType.equals(SchemaType.EDGE)) {
                JSONObject edges = putObject(ret, IMPORTER_KEY_EDGES);
                rows.forEach(row->{
                    if (row.containsKey(Keys._FROM)) {
                        String fromPoint = (String) row.get(Keys._FROM);
                        row.remove(Keys._FROM);
                        String[] fromArr = fromPoint.split("/");
                        String fromSchema = fromArr[0];
                        String fromId = fromArr[1];
                        JSONObject fromSchemaObject = putObject(edges, fromSchema);
                        JSONObject fromIdObject = putObject(fromSchemaObject, fromId);
                        JSONObject edgeObject = putObject(fromIdObject, schema);
                        String endPoint = (String) row.get(Keys._TO);
                        row.remove(Keys._TO);
                        String[] endArr = endPoint.split("/");
                        String endSchema = endArr[0];
                        String endId = endArr[1];
                        JSONObject endSchemaObject = putObject(edgeObject, endSchema);
                        JSONObject endIdObject = putObject(endSchemaObject, endId);
                        putAttributes(row, endIdObject);
                    }
                });
            }
        }
        return ret;
    }

    public static List<String> wrapDelete(GdbSuo suo, String url){
        List<String> ret = new ArrayList<>();
        String graph = suo.getGraph();
        String schema = suo.getSchema();
        SchemaType schemaType = suo.getType();
        List<Map<String, Object>> rows = suo.getRows();
        if(CollectionUtils.isEmpty(rows)){
            return ret;
        }
        if(Objects.nonNull(graph) && Objects.nonNull(schema)) {
            if (schemaType.equals(SchemaType.VERTEX)) {
                rows.forEach(row->{
                    if (row.containsKey(Keys.OBJECT_KEY)) {
                        String key = (String) row.get(Keys.OBJECT_KEY);
                        String deleteUrl = url + "/" + graph + "/vertices/" + schema + "/" + key;
                        ret.add(deleteUrl);
                    }
                });
            } else if (schemaType.equals(SchemaType.EDGE)) {
                for (Map<String, Object> row : rows) {
                    try {
                        String fromPoint = (String) row.get(Keys._FROM);
                        String[] fromArr = fromPoint.split("/");
                        String fromSchema = fromArr[0];
                        String fromId = fromArr[1];

                        String endPoint = (String) row.get(Keys._TO);
                        String[] endArr = endPoint.split("/");
                        String endSchema = endArr[0];
                        String endId = endArr[1];

                        String deleteUrl = url + "/" + graph + "/edges/"
                                + fromSchema + "/" + fromId
                                + "/" + schema + "/"
                                + endSchema + "/" + endId;
                        ret.add(deleteUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }

    // TODO: GQuery -> Set<String> should transform to Set<Object>, mark at 2019/3/13 18:50
    public static String wrapGQuery(GQuery gQuery){
        try {
            Map<String, Object> filter = gQuery.getFilter();
            String filterExpression = wrapExpression(filter);
            gQuery.setFilterExpression(filterExpression);
//            gQuery.setFilterExpression(URLEncoder.encode(filterExpression,"UTF-8"));
            LOG.info("filterExpression: \n{0}", filterExpression);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSONObject.toJSONString(gQuery);
    }


    ///////////////////////
    // private functions
    ///////////////////////
    private static void putAttributes(Map<String, Object> row, JSONObject attributes){
        for (String rowKey : row.keySet()) {
            Object value = row.get(rowKey);
            JSONObject attribute = new JSONObject();
            attribute.put("value", value);
            attributes.put(rowKey, attribute);
        }
    }

    private static JSONObject putObject(JSONObject ret, String key){
        if(!ret.containsKey(key)){
            ret.put(key, new JSONObject());
        }
        return ret.getJSONObject(key);
    }

    private static String wrapExpression(Map<String, Object> filter){
        StringBuilder expression = new StringBuilder();
        TigerQBuilderImpl tigerQBuilderImpl = new TigerQBuilderImpl();
        tigerQBuilderImpl.buildFilter(expression, filter);
        return expression.toString();
    }

    private static  Set<Object> wrapVertices(Set<String> vertices){
        Set<Object> ret = new HashSet<>();
        for (String vertexStr : vertices) {
            String[] vertexArr = vertexStr.split("/");
            JSONObject vertex = new JSONObject();
            vertex.put("type", vertexArr[0]);
            vertex.put("id", vertexArr[1]);
            ret.add(vertex);
        }
        return ret;
    }

}




