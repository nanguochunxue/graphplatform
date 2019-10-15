package com.haizhi.graph.plugins.flume.source.file.core;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.Getter;

import java.util.Map;

/**
 * Created by chengmo on 2018/12/13.
 */
public class Settings {

    private Map<String, Object> settings;

    public Settings(String jsonStr){
        settings = JSONUtils.jsonToMap(jsonStr);
    }

    public String getAsString(String setting){
        return Getter.get(setting, settings);
    }
}
