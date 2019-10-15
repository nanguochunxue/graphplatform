package com.haizhi.graph.dc.core.constant;

import javax.persistence.AttributeConverter;

/**
 * Created by chengangxiong on 2019/02/12
 */
public enum TaskState {

    NORMAL(1),PAUSED(2),TRIGGER(3),RUNNING(4);

    private int state;

    TaskState(int state){
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static class Convert implements AttributeConverter<TaskState, Integer> {

        @Override
        public Integer convertToDatabaseColumn(TaskState attribute) {
            return attribute.getState();
        }

        @Override
        public TaskState convertToEntityAttribute(Integer dbData) {
            for (TaskState ts : TaskState.values()){
                if (dbData == ts.getState()){
                    return ts;
                }
            }
            throw new RuntimeException("cannot convert [" + dbData + "] to TaskState(Enum)");
        }
    }
}
