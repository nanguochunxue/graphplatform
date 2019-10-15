package com.haizhi.graph.tag.analytics.util;

import com.haizhi.graph.common.constant.FieldType;

/**
 * Created by chengmo on 2018/4/18.
 */
public class FieldTypeUtils {

    public static String toHiveDataType(String fieldType){
        FieldType type;
        try {
            type = FieldType.valueOf(fieldType.toUpperCase());
        } catch (IllegalArgumentException e) {
            type = FieldType.STRING;
        }
        return toHiveDataType(type);
    }

    public static String toHiveDataType(FieldType type){
        switch (type){
            case STRING:
                return "string";
            case LONG:
                return "bigint";
            case DOUBLE:
                return "double";
            case DATETIME:
                return "string";
        }
        return "string";
    }

    public static FieldType getFieldTypeByHive(String hiveDataType){
        switch (hiveDataType){
            case "string":
            case "char":
            case "varchar":
                return FieldType.STRING;
            case "tinyint":
            case "smallint":
            case "int":
            case "bigint":
                return FieldType.LONG;
            case "float":
            case "double":
            case "decimal":
                return FieldType.DOUBLE;
            case "timestamp":
            case "date":
                return FieldType.DATETIME;
        }
        return FieldType.STRING;
    }
}
