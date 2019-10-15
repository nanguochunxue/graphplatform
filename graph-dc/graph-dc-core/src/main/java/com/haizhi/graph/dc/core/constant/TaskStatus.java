package com.haizhi.graph.dc.core.constant;

import com.haizhi.graph.common.constant.Status;

/**
 * Created by chengangxiong on 2019/01/29
 */
public enum TaskStatus implements Status {

    TASK_PAGE_ERROR(8000, "任务分页查询失败"),
    TASK_SAVE_ERROR(8001, "任务保存失败"),
    TASK_NOT_EXISTS(8001, "任务不存在"),
    TASK_INSTANCE_NOT_EXISTS(8001, "任务实例不存在"),
    TASK_INSTANCE_NOT_RUNNING(8001, "任务实例不在运行状态"),
    TASK_DELETE_ERROR(8002, "任务删除失败"),
    FILE_UPLOAD_ERROR(8003, "文件上传失败"),
    CRON_EXPRESSION_NULL(8004, "cron表达式不能为空"),
    CRON_EXPRESSION_ERROR(8005, "cron表达式不合法"),
    TASK_TYPE_ILLEGAL(8006, "任务类型不合法"),
    TASK_SUBMIT_ERROR(8007, "任务提交失败"),
    INIT_UPLOAD_HDFS_SRV_ERROR(8008, "初始化文件上传（HDFS）服务失败"),
    TASK_SUBMIT_FAIL_WAIT_TASK_REACH_MAX(8009, "任务提交失败，待执行任务数超过最大限制"),
    TASK_INSTANCE_PAGE_ERROR(8010, "任务实例分页查询失败"),
    TASK_OPERATE_TYPE_NULL(8011, "立即执行的任务，操作类型不能为空"),
    TASK_RUNONCE_ERROR(8012, "提交任务运行失败"),
    TASK_STOP_ERROR(8013, "暂停任务失败"),
    FILE_PATH_NOT_EXISTS(8014, "文件路径无效，请检查路径是否正确"),
    FILE_READ_ERROR(80015, "读取目标文件出错，请检查文件"),
    FILE_DIR_READ_ERROR(80016, "读取目标文件夹出错，请检查文件夹"),
    FILE_FORMAT_NOT_SUPPORT(80017, "不支持的文件格式，请使用JSON、CSV格式的文件"),
    FILE_NAME_TOO_LONG(80018, "文件名不能超过50个字节"),
    TASK_RUNNING_CANNOT_SUBMIT(80019, "任务正在运行，无法操作执行任务"),
    TASK_RUNNING_CANNOT_DELETE(80020, "任务正在运行，无法操作删除任务"),
    TASK_RUNNING_CANNOT_RUNONCE(80021, "任务正在运行，无法操作删除任务"),
    TASK_NOT_RUNNING_CANNOT_PAUSE(80022, "任务没有运行，无法操作终止任务"),
    TASK_PAUSE_ERROR(8023, "禁用任务失败"),
    TASK_NOT_CRON_CANNOT_PAUSE(8024, "非定时任务，无法操作禁用任务"),
    TASK_FILE_ILLEGAL(8025, "目录下存在非JSON、CSV格式文件:{0}"),
    TASK_DIR_NOT_CONTAIN_FILE(8026, "目录下没有文件"),
    ZERO_SIZE_TASK_FILE(8027, "任务文件长度为0"),
    TASK_FILE_UPLOAD_ZERO(8028, "任务文件上传失败"),
    UNSUPPORTED_STORE_TYPE_TASK_FILE(8029, "不支持的任务文件存储类型"),
    TASK_FILE_NOT_FOUND(8030, "找不到任务文件"),
    TASK_ERROR_DETAIL_NOT_FOUND(8031,"找不到错误详情"),
    TASK_DETAIL_NOT_FOUND(8032,"找不到任务详情"),
    TASK_NOT_FOUND(8033,"找不到任务"),
    TASK_INSTANCE_NOT_FOUND(8034,"找不到任务实例详情"),
    ;

    private int code;
    private String desc;

    TaskStatus(int code, String desc) {
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
