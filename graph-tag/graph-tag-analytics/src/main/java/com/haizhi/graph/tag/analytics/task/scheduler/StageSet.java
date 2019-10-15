package com.haizhi.graph.tag.analytics.task.scheduler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/10.
 */
public class StageSet {

    private Map<String, Stage.Task> tasks = new LinkedHashMap<>();
    private int taskIdCounter = 0;

    public StageSet() {
    }

    public void changeState(String taskId, Stage.TaskState state){
        Stage.Task task = tasks.get(taskId);
        if (task != null){
            task.setState(state);
        }
    }

    public void addTasks(Collection<Stage.Task> tasks){
        if (tasks == null){
            return;
        }
        for (Stage.Task task : tasks) {
            this.addTask(task);
        }
    }

    public void addTask(Stage.Task task){
        String taskId = task.getStageId() + "_" + task.getPriority();
        task.setTaskId(taskId + "_" + ++taskIdCounter);
        this.tasks.put(task.getTaskId(), task);
    }

    public Stage.Task getTask(String taskId){
        //int stageId = NumberUtils.toInt(StringUtils.substringBefore(taskId, ":"));
        return tasks.get(taskId);
    }

    public Map<String, Stage.Task> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, Stage.Task> tasks) {
        if (tasks == null){
            return;
        }
        this.tasks = tasks;
    }
}
