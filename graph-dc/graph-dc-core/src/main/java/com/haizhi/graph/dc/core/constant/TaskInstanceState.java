package com.haizhi.graph.dc.core.constant;

import javax.persistence.AttributeConverter;

/**
 * Created by chengangxiong on 2019/02/12
 */
public enum TaskInstanceState {

    READY(1, "已就绪"),
    RUNNING(2, "导入中"),
    SUCCESS(3, "成功"),
    INTERRUPTED(4, "异常中断"),
    EXPORTING(5, "卸数中"),
    FAILED(6,"失败")
    ;

    private int state;
    private String desc;

    TaskInstanceState(int state, String desc){
        this.state = state;
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public static class Converter implements AttributeConverter<TaskInstanceState, Integer> {

        @Override
        public Integer convertToDatabaseColumn(TaskInstanceState attribute) {
            return attribute.getState();
        }

        @Override
        public TaskInstanceState convertToEntityAttribute(Integer dbData) {
            for (TaskInstanceState ts : TaskInstanceState.values()){
                if (dbData == ts.getState()){
                    return ts;
                }
            }
            throw new RuntimeException("cannot convert [" + dbData + "] to TaskState(Enum)");
        }
    }
}
