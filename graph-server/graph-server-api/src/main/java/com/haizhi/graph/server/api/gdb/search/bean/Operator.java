package com.haizhi.graph.server.api.gdb.search.bean;

/**
 * Created by chengmo on 2018/1/19.
 */
public enum Operator {

    /* Conditional operators */
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    EQUALS("=="),
    NOT_EQUALS("!="),
    LIKE("like"),
    NOT_LIKE("not like"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),
    BETWEEN("between"),
    NOT_BETWEEN("not between"),
    IN("in"),
    NOT_IN("not in"),
    INVALID(""),

    /* Logical operators */
    AND("and"),
    OR("or"),
    NOT("not"),

    TRUE("true")
    ;

    private String code;

    Operator(String code) {
        this.code = code;
    }

    public String get(){
        return code;
    }
}
