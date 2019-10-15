package com.haizhi.graph.server.es.index.bean;

/**
 * Created by chengmo on 2018/1/11.
 */
public enum UpdateMode {
    INCREMENT("+="),            //Increment
    DECREMENT("-="),            //Decrement
    REPLACE("=");               //Replace

    private String value;

    UpdateMode(String value)
    {
        this.value = value;
    }

    public String toString()
    {
        return value;
    }
}
