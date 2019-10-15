package com.haizhi.graph.server.api.hbase.query.bean;

/**
 * Created by chengmo on 2018/5/9.
 */
public enum Stats {
    COUNT, MIN, MAX, AVG, SUM;

    public static Stats fromName(String name){
        try {
            return Stats.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
