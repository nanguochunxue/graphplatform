package com.haizhi.graph.server.es.index.mapping;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2017/11/30.
 */
@Data
public class TypeMapping implements Serializable {

    private static final GLog Log = LogFactory.getLogger(TypeMapping.class);

    private String indexName;
    private String typeName;
    private Map<String, Field> properties = new LinkedHashMap<>();

    public TypeMapping(String indexName, String typeName) {
        this.indexName = indexName;
        this.typeName = typeName;
    }

    public void addField(String field, DataType type) {
        properties.put(field, new Field(field, type));
    }

    public void addField(Field field) {
        properties.put(field.getName(), field);
    }

    public void addFields(Collection<Field> fields) {
        if (fields == null) {
            return;
        }
        Iterator<Field> iter = fields.iterator();
        while (iter.hasNext()) {
            Field field = iter.next();
            properties.put(field.getName(), field);
        }
    }

    public void addFields(Map<String, Field> fields) {
        if (fields == null) {
            return;
        }
        properties.putAll(fields);
    }

    public void setProperties(Map<String, Field> properties) {
        if (properties == null) {
            return;
        }
        this.properties = properties;
    }

    public XContentBuilder builder() {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.startObject("properties");
            for (Field f : properties.values()) {
                builder.startObject(f.getName());

                // children-> has properties
                if (f.hasProperties()) {
                    builder.startObject("properties");
                    for (Field pf : f.getProperties().values()) {
                        builder.startObject(pf.getName());
                        builder.field("type", pf.getType().code());
                        if (StringUtils.isNotBlank(f.getAnalyzer())) {
                            builder.field("analyzer", f.getAnalyzer());
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                } else {
                    DataType fieldType = f.getType();
                    builder.field("type", fieldType.code());
                    if (DataType.STRING == fieldType || DataType.KEYWORD == fieldType) {
                        builder.startObject("fields");
                            builder.startObject("keyword");
                                builder.field("type", "keyword");
                            builder.endObject();
                        builder.endObject();
                    }

                    if (StringUtils.isNotBlank(f.getAnalyzer())) {
                        builder.field("analyzer", f.getAnalyzer());
                    }
                }
                builder.endObject();
            }
            builder.endObject();
            builder.endObject();
        } catch (IOException e) {
            Log.error(e);
        }
        return builder;
    }

    @Data
    public static class Field implements Serializable {

        private String name;
        private DataType type;
        private String analyzer;
        private String searchAnalyzer;
        private Map<String, Field> properties = new LinkedHashMap<>();
        private Map<String, Field> fields = new LinkedHashMap<>();

        public Field() {
        }

        public Field(String name, DataType type) {
            this.name = name;
            this.type = type;
            this.initializeAnalyzer();
        }

        public void addProperty(String field, DataType type) {
            this.properties.put(field, new Field(field, type));
        }

        public boolean hasProperties() {
            return !properties.isEmpty();
        }

        public Map<String, Field> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Field> properties) {
            if (properties == null) {
                return;
            }
            this.properties = properties;
        }

        public Map<String, Field> getFields() {
            return fields;
        }

        public void setFields(Map<String, Field> fields) {
            if (fields == null) {
                return;
            }
            this.fields = fields;
        }

        private void initializeAnalyzer() {
            if (DataType.STRING == type) {
                this.analyzer = Analyzers.IK.code();
            }
        }
    }
}
