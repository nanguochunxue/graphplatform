package com.haizhi.graph.common.constant;

/**
 * Created by linzhihuang on 2017/8/5.
 */
public enum IndexTypeEnum {
    HASHINDEX(0), FULLTEXTINDEX(1), GEOINDEX(2),
    PERSISTENTINDEX(3),SKIPLISTINDEX(4);
    public int TABLE_NAME;

    IndexTypeEnum(int table) {
        this.TABLE_NAME = table;
    }
}
