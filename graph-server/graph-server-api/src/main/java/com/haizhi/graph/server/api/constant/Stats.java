package com.haizhi.graph.server.api.constant;

/**
 * Created by chengmo on 2018/8/16.
 */
public enum Stats {
    COUNT, MIN, MAX, AVG, SUM, STATS;

    public static Stats fromName(String name){
        try {
            return Stats.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
