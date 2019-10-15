package com.haizhi.graph.server.api.gdb.search.xcontent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by chengmo on 2018/1/22.
 */
public class XContentBuilder {

    private JSONObject rootObject;
    private JSONObject rootObjectValue;

    private XContentBuilder(String name){
        this.rootObjectValue = jsonObject();
        this.rootObject = jsonObject();
        this.rootObject.put(name, rootObjectValue);
    }

    public JSONObject rootObject(){
        return rootObject;
    }

    public JSONObject getJSONObject(String key){
        JSONObject value = rootObjectValue.getJSONObject(key);
        if (value == null){
            return new JSONObject();
        }
        return value;
    }

    public JSONArray getJSONArray(String key){
        JSONArray value = rootObjectValue.getJSONArray(key);
        if (value == null){
            return new JSONArray();
        }
        return value;
    }

    private String getString(String key){
        return rootObjectValue.getString(key);
    }

    private int getInt(String key){
        return rootObjectValue.getIntValue(key);
    }

    private long getLong(String key){
        return rootObjectValue.getLongValue(key);
    }

    private float getFloat(String key){
        return rootObjectValue.getFloatValue(key);
    }

    private double getDouble(String key){
        return rootObjectValue.getDoubleValue(key);
    }

    public XContentBuilder put(String key, JSONObject value){
        return this.putObject(key, value);
    }

    public XContentBuilder put(String key, JSONArray value){
        return this.putObject(key, value);
    }

    public XContentBuilder put(String key, String value){
        return this.putObject(key, value);
    }

    public XContentBuilder put(String key, int value){
        return this.putObject(key, value);
    }

    public XContentBuilder put(String key, long value){
        return this.putObject(key, value);
    }

    public XContentBuilder put(String key, float value){
        return this.putObject(key, value);
    }

    public XContentBuilder put(String key, double value){
        return this.putObject(key, value);
    }

    private XContentBuilder putObject(String key, Object value){
        ensureNameNotNull(key);
        this.rootObjectValue.put(key, value);
        return this;
    }

    public String string() {
        return JSON.toJSONString(rootObject, true);
    }

    public static XContentBuilder builder(String name){
        ensureNameNotNull(name);
        return new XContentBuilder(name);
    }

    public static String getFirstKey(JSONObject object){
        if (!object.keySet().isEmpty()){
            return object.keySet().iterator().next();
        }
        return "";
    }

    public static JSONObject jsonObject(){
        return new JSONObject(true);
    }

    public static JSONArray jsonArray(){
        return new JSONArray();
    }

    static void ensureNameNotNull(String name) {
        ensureNotNull(name, "key name cannot be null");
    }

    static void ensureNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
