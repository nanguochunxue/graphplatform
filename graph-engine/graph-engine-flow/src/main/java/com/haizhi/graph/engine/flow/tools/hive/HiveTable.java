package com.haizhi.graph.engine.flow.tools.hive;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/4/16.
 */
public class HiveTable {

    private String tableName;
    private List<Field> fields = new ArrayList<>();

    public HiveTable() {
    }

    public HiveTable(String tableName) {
        this.tableName = tableName;
    }

    public void addField(Field field){
        this.fields.add(field);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        if (fields == null){
            return;
        }
        this.fields = fields;
    }

    public static class Field{
        private String fieldName;
        private String dataType;

        public Field() {
        }

        public Field(String fieldName, String dataType) {
            this.fieldName = fieldName;
            this.dataType = dataType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }
}
