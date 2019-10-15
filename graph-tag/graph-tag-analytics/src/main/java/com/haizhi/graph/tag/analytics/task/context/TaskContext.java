package com.haizhi.graph.tag.analytics.task.context;

import com.haizhi.graph.tag.analytics.bean.Partitions;

import java.util.*;

/**
 * Created by chengmo on 2018/4/25.
 */
public class TaskContext {

    private String graph;
    private TaskType taskType;
    private Set<Long> tagIds = new LinkedHashSet<>();
    private Partitions partitions = new Partitions();
    private boolean partitionsEnabled;

    public TaskContext() {
    }

    public TaskContext(String graph) {
        this.graph = graph;
    }

    public void addTagIds(Long... tagIds){
        this.tagIds.addAll(Arrays.asList(tagIds));
    }

    public void addTagIds(Collection<Long> tagIds){
        this.tagIds.addAll(tagIds);
    }

    public void addPartitions(Map<String, List<String>> partitions){
        this.partitions.addPartitions(partitions);
    }

    public void addRangePartition(String name, String from, String to){
        this.partitions.addRangePartition(name, from, to);
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Partitions getPartitions() {
        return partitions;
    }

    public void setPartitions(Partitions partitions) {
        this.partitions = partitions;
    }

    public boolean isPartitionsEnabled() {
        return partitionsEnabled;
    }

    public void setPartitionsEnabled(boolean partitionsEnabled) {
        this.partitionsEnabled = partitionsEnabled;
    }
}
