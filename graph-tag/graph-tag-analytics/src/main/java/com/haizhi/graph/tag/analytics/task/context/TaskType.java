package com.haizhi.graph.tag.analytics.task.context;

/**
 * Created by chengmo on 2018/4/23.
 */
public enum TaskType {
    /**
     * Full task.
     */
    FULL {
        @Override
        public String toString() {
            return "Full";
        }
    },

    /**
     * Increment task.
     */
    INC {
        @Override
        public String toString() {
            return "Increment";
        }
    },

    /**
     * Dag task.
     */
    DAG {
        @Override
        public String toString() {
            return "Dag";
        }
    };

    public static TaskType fromName(String taskType){
        if (taskType == null) {
            return null;
        }
        try {
            return TaskType.valueOf(taskType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
