package com.haizhi.graph.common.log;

/**
 * Created by chengmo on 2017/12/15.
 */
public class LogFactory {
    public static GLog getLogger(Class clazz){
        return new GLog(clazz);
    }
}
