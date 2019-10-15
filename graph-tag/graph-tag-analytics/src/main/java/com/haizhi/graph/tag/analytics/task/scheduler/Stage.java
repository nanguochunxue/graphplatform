package com.haizhi.graph.tag.analytics.task.scheduler;

import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by chengmo on 2018/4/8.
 */
public class Stage implements Serializable {

    public enum TaskState {
        SCHEDULED,
        RUNNING,
        SUCCEEDED,
        FAILED,
        KILLED
    }

    public static class Task implements Serializable {

        private String taskId;
        private int stageId;
        private int priority;
        private Set<Long> tagIds = new LinkedHashSet<>();

        private FlowTask flowTask;
        private String appId;
        private TaskState state = TaskState.SCHEDULED;

        public Task addTagId(long tagId){
            tagIds.add(tagId);
            return this;
        }

        public String getTagIdsString(){
            StringBuilder sb = new StringBuilder();
            int limit = 3;
            if (tagIds.size() > limit){
                for (Long tagId : tagIds) {
                    if (limit < 0){
                        break;
                    }
                    sb.append(tagId).append(",");
                    limit--;
                }
                sb = sb.delete(sb.length() - 1, sb.length()).append("...");
            } else {
                for (Long tagId : tagIds) {
                    sb.append(tagId).append(",");
                }
                sb = sb.delete(sb.length() - 1, sb.length());
            }
            return sb.toString();
        }

        public boolean isEmpty(){
            return tagIds.isEmpty();
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public int getStageId() {
            return stageId;
        }

        public void setStageId(int stageId) {
            this.stageId = stageId;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public Set<Long> getTagIds() {
            return tagIds;
        }

        public void setTagIds(Set<Long> tagIds) {
            if (tagIds == null){
                return;
            }
            this.tagIds = tagIds;
        }

        public FlowTask getFlowTask() {
            return flowTask;
        }

        public void setFlowTask(FlowTask flowTask) {
            this.flowTask = flowTask;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public TaskState getState() {
            return state;
        }

        public void setState(TaskState state) {
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Task task = (Task) o;

            return taskId != null ? taskId.equals(task.taskId) : task.taskId == null;
        }

        @Override
        public int hashCode() {
            return taskId != null ? taskId.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "taskId='" + taskId + '\'' +
                    ", tagIds=" + tagIds +
                    ", state=" + state +
                    '}';
        }
    }
}
