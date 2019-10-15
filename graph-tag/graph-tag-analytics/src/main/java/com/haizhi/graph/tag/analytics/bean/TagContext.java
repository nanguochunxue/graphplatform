package com.haizhi.graph.tag.analytics.bean;

import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.tag.analytics.task.scheduler.StageSet;
import com.haizhi.graph.tag.core.bean.TagDomain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/9.
 */
public class TagContext {

    private TagDomain tagDomain;
    private Domain domain;
    private String taskIdPrefix;
    private StageSet stageSet;
    private String kafkaTopic;
    private String locationConfig;
    private Partitions partitions = new Partitions();
    private Map<String, String> properties = new HashMap<>();

    private boolean debugEnabled;

    /** security */
    private boolean securityEnabled;
    private String userPrincipal;
    private String userConfPath;
    private String bdpVersion;

    public TagContext() {
    }

    public TagContext(TagDomain tagDomain, Domain domain) {
        this.tagDomain = tagDomain;
        this.domain = domain;
    }

    public void putProperty(String key, String value){
        this.properties.put(key, value);
    }

    public void putProperties(Map<String, String> properties){
        if (properties != null){
            this.properties.putAll(properties);
        }
    }

    public TagDomain getTagDomain() {
        return tagDomain;
    }

    public void setTagDomain(TagDomain tagDomain) {
        this.tagDomain = tagDomain;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getTaskIdPrefix() {
        return taskIdPrefix;
    }

    public void setTaskIdPrefix(String taskIdPrefix) {
        this.taskIdPrefix = taskIdPrefix;
    }

    public StageSet getStageSet() {
        return stageSet;
    }

    public void setStageSet(StageSet stageSet) {
        this.stageSet = stageSet;
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

    public Partitions getPartitions() {
        return partitions;
    }

    public void setPartitions(Partitions partitions) {
        if (partitions == null){
            return;
        }
        this.partitions = partitions;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        if (properties == null){
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

    public String getBdpVersion() {
        return bdpVersion;
    }

    public void setBdpVersion(String bdpVersion) {
        this.bdpVersion = bdpVersion;
    }
}


