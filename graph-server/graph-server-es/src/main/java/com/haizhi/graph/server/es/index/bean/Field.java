package com.haizhi.graph.server.es.index.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by chengmo on 2018/1/11.
 */
@Data
public class Field {

    private String name;
    private Object value;
    private UpdateMode updateMode;

    public Field(String name, Object value) {
        this(name, value, UpdateMode.REPLACE);
    }

    public Field(String name, Object value, UpdateMode updateMode) {
        this.name = name;
        if (value instanceof BigDecimal) {
            value = ((BigDecimal) value).doubleValue();
        }
        this.value = value;
        this.updateMode = updateMode == null ? UpdateMode.REPLACE : updateMode;
    }

    @Override
    public String toString() {
        return "Field [name=" + name + ", value=" + value + ", updateMode=" + updateMode + "]";
    }
}
