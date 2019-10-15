package com.haizhi.graph.tag.analytics.engine.conf;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/3.
 */
public class FlowTaskSchema implements Serializable {

    private String schema;
    private String schemaType;
    private String reduceKey;
    private String startRow;
    private String stopRow;
    private DataSourceType sourceType = DataSourceType.HBASE;
    private Map<String, Field> fields = new LinkedHashMap<>();
    private String sql;

    public FlowTaskSchema() {
    }

    public FlowTaskSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @param field
     * @return
     */
    public FlowTaskSchema addField(String field) {
        this.fields.put(field, new Field(field));
        return this;
    }

    /**
     * @param field
     * @param dataType
     * @return
     */
    public FlowTaskSchema addField(String field, String dataType) {
        this.fields.put(field, new Field(field, dataType));
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String getReduceKey() {
        return reduceKey;
    }

    /**
     * @param reduceKey family:field (HBase)
     */
    public void setReduceKey(String reduceKey) {
        this.reduceKey = reduceKey;
    }

    public String getStartRow() {
        return startRow;
    }

    public void setStartRow(String startRow) {
        this.startRow = startRow;
    }

    public String getStopRow() {
        return stopRow;
    }

    public void setStopRow(String stopRow) {
        this.stopRow = stopRow;
    }

    public DataSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(DataSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setFields(Map<String, Field> fields) {
        if (fields == null) {
            return;
        }
        this.fields.clear();
        this.fields.putAll(fields);
    }

    public static class Field implements Serializable {
        private String field;
        private String dataType;

        public Field() {
        }

        public Field(String field) {
            this.field = field;
        }

        public Field(String field, String dataType) {
            this.field = field;
            this.dataType = dataType;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }
}
