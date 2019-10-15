package com.haizhi.graph.dc.core.constant;


/**
 * Created by chengangxiong on 2019/01/29
 */
public enum TaskType {

    FILE("文件导入"),
    HDFS("HDFS导入"),
    HIVE("HIVE导入"),
    GREEPLUM("GP导入"),
    FLUME(""),
    API(""),
    BATCH("");

    private String desc;

    TaskType(String desc){
        this.desc = desc;
    }

    public static TaskType transferFromInboundType(DcInboundType inboundType) {
        if (inboundType == DcInboundType.TASK) {
            throw new RuntimeException("not supported for transfer DcInboundType.TASK to TaskType");
        } else {
            switch (inboundType){
                case BATCH_PLUGIN:
                    return BATCH;
                case FLUME_PLUGIN:
                    return FLUME;
                case API:
                    return API;
                default:
                    throw new RuntimeException("not supported for [" + inboundType.name() + "] to transfer");
            }
        }
    }
}
