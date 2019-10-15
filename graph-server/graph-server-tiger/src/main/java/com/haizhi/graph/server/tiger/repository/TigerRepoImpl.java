package com.haizhi.graph.server.tiger.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.rest.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.haizhi.graph.server.tiger.constant.TigerKeys.*;


/**
 * Created by chengmo on 2019/3/6.
 */
@Repository
public class TigerRepoImpl implements TigerRepo {

    private static final GLog LOG = LogFactory.getLogger(TigerRepoImpl.class);

    @Autowired
    private RestService restService;

    @Override
    public JSONObject execute(String graphUrl) {
        JSONObject ret = new JSONObject();
        try {
            return restService.doPost(graphUrl, JSONObject.class);
        }catch (Exception e){
            ret.put(RESPONSE_JSON_ERROR, true);
            ret.put(RESPONSE_JSON_EXCEPTION, e.getMessage());
            LOG.error(e);
        }
        return ret;
    }

    @Override
    public JSONObject executeDelete(String graphUrl) {
        JSONObject ret = new JSONObject();
        try {
            return restService.doDelete(graphUrl, null, JSONObject.class);
        }catch (Exception e){
            ret.put(RESPONSE_JSON_ERROR, true);
            ret.put(RESPONSE_JSON_EXCEPTION, e.getMessage());
            LOG.error(e);
        }
        return ret;
    }

    @Override
    public JSONObject executeUpsert(String graphUrl, Map<String, Object> data) {
        JSONObject ret = new JSONObject();
        try {
            return restService.doPost(graphUrl, data, JSONObject.class);
        } catch (Exception e) {
            ret.put(RESPONSE_JSON_ERROR, true);
            ret.put(RESPONSE_JSON_EXCEPTION, e.getMessage());
            LOG.error(e);
        }
        return ret;
    }

    /**
     * execute tiger graph sql
     * url: http://192.168.1.234:14240/gsqlserver/gsql/file#tigergraph:tigergraph
     * @param url:  tiger rest api
     * @param gsql: tiger graph sql
     * @return: JSONObject
     * this method support CREATE or DROP the schema: graph or table
     */
    @Override
    @SuppressWarnings("all")
    public JSONObject execute(String graphSqlUrl, String graphSql) {
        JSONObject ret = new JSONObject();
        try {
            Map<String, Object> urlHeaders = rebuildUrl(graphSqlUrl);
            String rawUrl = (String)urlHeaders.get(PARAMETER_URL);
            Map<String, String> headers = (Map<String, String>)urlHeaders.get(PARAMETER_HEADERS);
            String payload = URLEncoder.encode(graphSql, PARAMETER_CHARSET);
            String tigerResponse = restService.doPost(rawUrl, payload, String.class, headers);
            return buildResult(tigerResponse);
        } catch (Exception e) {
            ret.put(RESPONSE_JSON_ERROR, true);
            ret.put(RESPONSE_JSON_EXCEPTION, e.getMessage());
            LOG.error(e);
        }
        return ret;
    }

    /*

    * */
    @Override
    public JSONObject executeQuery(String searchUrl, Map<String, String> parameters) {
        JSONObject ret = new JSONObject();
        try {
            ret = restService.doGet(searchUrl, parameters, JSONObject.class);
        } catch (Exception e) {
            ret.put(RESPONSE_JSON_ERROR, true);
            ret.put(RESPONSE_JSON_EXCEPTION, e.getMessage());
            LOG.info("graphUrl: {0}", searchUrl);
            LOG.error(e);
        }
        return ret;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private Map<String, Object> rebuildUrl(String url) {
        Map<String, Object> urlHeaders = new HashMap<>();
        String[] urlParameters = url.split(PARAMETER_HEADER_SPLIT);
        String rawUrl = urlParameters[0];
        Map<String, String> headers = new HashMap<>();
        String account = urlParameters[1];
        String token = getBase64(account);
        headers.put(PARAMETER_HEADER_AUTH, PARAMETER_HEADER_BASIC + token);
        JSONObject session = new JSONObject();
        session.put(PARAMETER_HEADER_SESSION, PARAMETER_HEADER_EMPTY);
        headers.put(PARAMETER_HEADER_COOKIE, session.toJSONString());
        urlHeaders.put(PARAMETER_URL, rawUrl);
        urlHeaders.put(PARAMETER_HEADERS, headers);
        return urlHeaders;
    }

    private static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes(PARAMETER_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    private static JSONObject buildResult(String tigerResponse){
        JSONObject ret = new JSONObject();
        StringBuilder result = new StringBuilder();
        String[] arr = tigerResponse.split(RESPONSE_JSON_SPLIT_1);
        boolean error = false;
        boolean checkVertexSchema = false;
        boolean checkEdgesSchema = false;
        boolean checkGraphsSchema = false;
        boolean checkJobsSchema = false;
        boolean checkQueriesSchema = false;
        for(String ele : arr) {
            String line = ele.trim();
            if(line.contains(RESPONSE_LINE_ENCOUNTERED)||line.contains(RESPONSE_LINE_NOT_EXIST)) {
                error = true;
            }else if(line.contains(RESPONSE_LINE_API_VERSION)){
                String[] version = line.split(RESPONSE_JSON_SPLIT_5);
                ret.put(RESPONSE_JSON_VERSION, version[1]);
            }else if(line.contains(RESPONSE_LINE_G_COOKIE)){
                String[] graphSqlCookie = line.split(RESPONSE_JSON_SPLIT_6);
                ret.put(RESPONSE_JSON_COOKIE, graphSqlCookie[1]);
                continue;
            }if(line.contains(RESPONSE_LINE_G_RETURN_CODE)) {
                String[] graphSqlReturnCode = line.split(RESPONSE_JSON_SPLIT_6);
                ret.put(RESPONSE_JSON_CODE, graphSqlReturnCode[1]);
                continue;
            }else if(line.contains(RESPONSE_LINE_G_Graph)|line.contains(RESPONSE_LINE_G_GLOBAL)){
                continue;
            }else if(line.contains(RESPONSE_LINE_VERTEX_TYPES)) {
                ret.put(RESPONSE_JSON_VERTICES, new JSONArray());
                checkVertexSchema = true;
                checkEdgesSchema = false;
                checkGraphsSchema = false;
                checkJobsSchema = false;
                checkQueriesSchema = false;
                continue;
            }else if(line.contains(RESPONSE_LINE_EDGE_EDGES)) {
                ret.put(RESPONSE_JSON_EDGES, new JSONArray());
                checkVertexSchema = false;
                checkEdgesSchema = true;
                checkGraphsSchema = false;
                checkJobsSchema = false;
                checkQueriesSchema = false;
                continue;
            }else if(line.contains(RESPONSE_LINE_GRAPHS)) {
                ret.put(RESPONSE_JSON_GRAPHS, new JSONArray());
                checkVertexSchema = false;
                checkEdgesSchema = false;
                checkGraphsSchema = true;
                checkJobsSchema = false;
                checkQueriesSchema = false;
                continue;
            }else if(line.contains(RESPONSE_LINE_JOBS)) {
                ret.put(RESPONSE_JSON_JOBS, new JSONArray());
                checkVertexSchema = false;
                checkEdgesSchema = false;
                checkGraphsSchema = false;
                checkJobsSchema = true;
                checkQueriesSchema = false;
                continue;
            }else if(line.contains(RESPONSE_LINE_QUERIES)) {
                ret.put(RESPONSE_JSON_QUERIES, new JSONArray());
                checkVertexSchema = false;
                checkEdgesSchema = false;
                checkGraphsSchema = false;
                checkJobsSchema = false;
                checkQueriesSchema = true;
                continue;
            }if(line.contains(RESPONSE_JSON_SPLIT_2)){
                String[] schemaArr = line.split(RESPONSE_JSON_SPLIT_3);
                String[] schemaNameArr = schemaArr[0].split(RESPONSE_JSON_SPLIT_4);
                String schemaName = schemaNameArr[schemaNameArr.length-1];
                if(checkVertexSchema) {
                    ret.getJSONArray(RESPONSE_JSON_VERTICES).add(schemaName);
                }else if(checkEdgesSchema){
                    ret.getJSONArray(RESPONSE_JSON_EDGES).add(schemaName);
                }else if(checkGraphsSchema){
                    ret.getJSONArray(RESPONSE_JSON_GRAPHS).add(schemaName);
                }else if(checkJobsSchema){
                    ret.getJSONArray(RESPONSE_JSON_JOBS).add(schemaName);
                }else if(checkQueriesSchema){
                    ret.getJSONArray(RESPONSE_JSON_QUERIES).add(schemaName);
                }
                continue;
            }
            result.append(line);
        }
        ret.put(RESPONSE_JSON_RESULT, result);
        ret.put(RESPONSE_JSON_ERROR, error);
        return ret;
    }

}
