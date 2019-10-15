package com.haizhi.graph.engine.base.rule.script;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/2.
 */
public class ScriptContext implements Serializable {

    public enum Type {
        VERTEX, EDGE, TAG, SQL
    }

    @JSONField(name = "metadata")
    private Map<String, Metadata> metadataMap;

    public Map<String, Metadata> getMetadataMap() {
        return metadataMap;
    }

    public void setMetadataMap(Map<String, Metadata> metadataMap) {
        this.metadataMap = metadataMap;
    }

    public static class Metadata {

        private String name;
        private Type type;
        private List<String> fields = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            if (fields == null){
                return;
            }
            this.fields.addAll(fields);
        }
    }
}
