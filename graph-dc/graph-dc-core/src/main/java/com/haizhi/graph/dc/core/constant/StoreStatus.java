package com.haizhi.graph.dc.core.constant;

import com.haizhi.graph.common.constant.Status;

/**
 * Created by chengangxiong on 2019/03/25
 */
public enum  StoreStatus implements Status {
    ENV_DELETE_FAIL(        10806, "环境删除失败"),
    ENV_FIND_FAIL(          10806, "环境查找失败"),
    ENV_SAVE_UPDATE_FAIL(   10806, "环境保存或更新失败"),
    ENV_CANNOT_DELETE(      10806, "无法删除该环境，此环境已被数据源使用"),

    ENV_FILE_DELETE_FAIL(   10806, "环境文件删除失败"),
    ENV_FILE_FIND_FAIL(     10806, "环境文件删除失败"),
    ENV_FILE_UPLOAD_FAIL(   10806, "环境文件上传失败"),
    ENV_FILE_NOT_EXISTS(    10806, "环境文件不存在"),

    STORE_NOT_DELETE(       10806,"该数据源已被使用")
    ;

    private int code;
    private String desc;

    StoreStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
