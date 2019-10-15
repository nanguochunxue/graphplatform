package com.haizhi.graph.tag.analytics.engine.conf;

import com.haizhi.graph.tag.analytics.bean.Partitions;

import java.io.Serializable;
import java.util.*;

/**
 * Created by chengmo on 2018/3/13.
 */
public class FlowTask implements Serializable {

    private String id;
    private ActionType action = ActionType.SPARK;
    private String graph;
    private Set<Long> tagIds = new LinkedHashSet<>();

    /** query filter by partitions */
    private Partitions partitions = new Partitions();

    /** stages */
    private Map<Integer, Stage> stages = new LinkedHashMap<>();
    private int stageSeqNumber = 1;

    /** config */
    private String kafkaTopic;
    private String locationConfig;
    private Map<String, String> properties = new HashMap<>();
    private boolean debugEnabled;

    /** security */
    private boolean securityEnabled;
    private String userPrincipal;
    private String userConfPath;

    public FlowTask() {
    }

    public FlowTask(String id) {
        this.id = id;
    }

    @Deprecated
    public boolean available() {
        return !stages.isEmpty();
    }

    /**
     * Added stage for stageId auto generation.
     *
     * @param stage
     * @return
     */
    public FlowTask addStage(Stage stage) {
        stages.put(stageSeqNumber, stage);
        stageSeqNumber++;
        return this;
    }

    /**
     * @param seqNumber start with 1
     * @return
     */
    public Stage getStage(int seqNumber) {
        return stages.get(seqNumber);
    }

    public FlowTask putProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public FlowTask putProperties(Map<String, String> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public long getTagId() {
        if (this.tagIds.isEmpty()){
            return 0;
        }
        return this.tagIds.iterator().next();
    }

    public void setTagId(long tagId) {
        this.tagIds.clear();
        this.tagIds.add(tagId);
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

    public Partitions getPartitions() {
        return partitions;
    }

    public void setPartitions(Partitions partitions) {
        this.partitions = partitions;
    }

    public Map<Integer, Stage> getStages() {
        return stages;
    }

    public void setStages(Map<Integer, Stage> stages) {
        if (stages == null) {
            return;
        }
        this.stages = stages;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getLocationConfig() {
        return locationConfig;
    }

    public void setLocationConfig(String locationConfig) {
        this.locationConfig = locationConfig;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        if (properties == null) {
            return;
        }
        this.properties = properties;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public String getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(String userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public String getUserConfPath() {
        return userConfPath;
    }

    public void setUserConfPath(String userConfPath) {
        this.userConfPath = userConfPath;
    }

    public static class Stage implements Serializable {

        private String id;
        private FlowTaskInput input;
        private FlowTaskOutput output;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public FlowTaskInput getInput() {
            return input;
        }

        public void setInput(FlowTaskInput input) {
            this.input = input;
        }

        public FlowTaskOutput getOutput() {
            return output;
        }

        public void setOutput(FlowTaskOutput output) {
            this.output = output;
        }
    }
}
