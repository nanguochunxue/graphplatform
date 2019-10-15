package com.haizhi.graph.search.api.gdb.result;

import lombok.Data;

/**
 * Created by chengmo on 2018/6/11.
 */
@Data
public class RElement {
    private Type type = Type.FIELD;
    private String expression;
    private String table;
    private String field;
    private String stringValue;

    public void resetValue(){
        this.stringValue = "";
    }

    public String getEscapeExpression(){
        if (expression == null){
            return null;
        }
        return expression.replace("(", "$").replace(")", "$").replace(".", "$");
    }

    public enum Type {
        FIELD,
        VALUE
    }
}
