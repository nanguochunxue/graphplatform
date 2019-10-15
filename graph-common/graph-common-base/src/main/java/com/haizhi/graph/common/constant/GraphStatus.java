package com.haizhi.graph.common.constant;

public enum GraphStatus implements Status {

    UNKNOWN(                    5001, "未知状态"),
    SUCCESS(                    5002, "success"),
    ERROR(                      5003, "error"),
    MISS_PARAM(                 5004, "缺少必要参数"),
    NOT_SUPPORTED(              5005, "暂不支持"),
    COPY_FAILE(                 5006,"复制新对象失败"),
    DETAIL_FAILE(               5007,"查询详情失败"),
    DElETE_FAILE(               5008,"删除失败"),
    SAVE_OR_UPDATE_FAIL(        5009,"保存失败"),
    BATCH_SAVE_OR_UPDATE_FAIL(  5010,"批量保存失败"),
    FIND_FAIL(                  5011,"查询失败"),
    FIND_PAGE_FAIL(             5012,"分页查询失败"),
    SAVE_PARAM_NULL(            5013,"保存对象不能为空"),
    DAO_NULL(                   5014,"Dao对象未指定"),
    BUILD_FILTER_FAIL(          5015, "组装过滤条件失败"),
    URL_INVALID_ERROR(          5016, "无效的URL"),
    CALL_REST_ERROR(            5017, "调用Rest接口异常,{0}"),
    REPEAT_ERROR(               5018, "操作失败，有重复的数据"),
    UN_AUTH(                    5019, "未授权"),

    UNAUTHORIZED(               401, "未认证用户"),
    FORBIDDEN(                  403, "未授权访问接口")
    ;


    private int code;
    private String desc;

    GraphStatus(int code, String desc) {
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
