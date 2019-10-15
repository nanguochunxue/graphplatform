package com.haizhi.graph.engine.flow.util;

import com.haizhi.graph.common.constant.FieldType;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by chengmo on 2018/3/16.
 */
public class DataUtils {

    public static Object parseObject(String value, String fieldType) {
        FieldType type;
        try {
            type = FieldType.valueOf(fieldType.toUpperCase());
        } catch (IllegalArgumentException e) {
            type = FieldType.STRING;
        }
        switch (type) {
            case STRING:
                return value == null ? "" : value;
            case DOUBLE:
                return NumberUtils.toDouble(value, 0);
            case LONG:
                return NumberUtils.toLong(value, 0);
            case DATETIME:
                return value == null ? "" : value;
        }
        return value;
    }
}
