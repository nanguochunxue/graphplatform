package com.haizhi.graph.server.es.index.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2017/12/29.
 */
@Data
@NoArgsConstructor
public class Source implements Serializable{

    private String id;
    private Map<String, Object> source = new LinkedHashMap<>();

    public Source addField(String field, Object value){
        if (field != null){
            source.put(field, value);
        }
        return this;
    }

    public void setSource(Map<String, Object> source) {
        if (source == null){
            return;
        }
        this.source.clear();
        this.source.putAll(source);
    }
}
