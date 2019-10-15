package com.haizhi.graph.tag.core.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.TagDependency;
import com.haizhi.graph.tag.core.domain.TagParameter;

import java.util.*;

/**
 * Created by chengmo on 2018/3/9.
 */
public class TagDomain {

    private String graphName;
    // tag
    private Map<Long, TagInfo> tagInfoMap = new LinkedHashMap<>();

    private List<TagSchemaInfo> tagSchemaInfoList = new ArrayList<>();

    private List<TagDependency> dependencies = new ArrayList<>();

    private Map<Long, TagParameter> parameters = new HashMap<>();

    public TagDomain() {
    }

    public TagDomain(String graphName) {
        this.graphName = graphName;
    }

    public TagInfo getTagInfo(long tagId){
        return tagInfoMap.get(tagId);
    }

    public TagParameter getTagParameter(long paramId){
        return this.parameters.get(paramId);
    }

    public Map<String, Set<String>> getTagSchemaNames(Set<Long> tagIds){
        Map<String, Set<String>> result = new LinkedHashMap<>();
        for (TagSchemaInfo info : tagSchemaInfoList) {
            if (!tagIds.contains(info.getTagId())){
                continue;
            }
            String schema = info.getSchema();
            Set<String> fields = result.get(schema);
            if (fields == null){
                fields = new LinkedHashSet<>();
                result.put(schema, fields);
            }
            String[] arr = info.getFields().split(",");
            fields.addAll(Arrays.asList(arr));
        }
        return result;
    }

    @JSONField(serialize = false)
    public Map<String, Set<String>> getAllTagSchemaNames(){
        Map<String, Set<String>> result = new LinkedHashMap<>();
        for (TagSchemaInfo info : tagSchemaInfoList) {
            String schema = info.getSchema();
            Set<String> fields = result.get(schema);
            if (fields == null){
                fields = new LinkedHashSet<>();
                result.put(schema, fields);
            }
            String[] arr = info.getFields().split(",");
            fields.addAll(Arrays.asList(arr));
        }
        return result;
    }

    public List<TagInfo> getTagInfoList(Set<Long> tagIds){
        List<TagInfo> results = new ArrayList<>();
        for (Long tagId : tagIds) {
            TagInfo tagInfo = tagInfoMap.get(tagId);
            results.add(tagInfo);
        }
        return results;
    }

    public List<TagInfo> getLogicTagInfoList(Collection<String> schemas){
        List<TagInfo> results = new ArrayList<>();
        if (schemas == null || schemas.isEmpty()){
            return results;
        }
        Set<Long> set = new HashSet<>();
        for (TagSchemaInfo info : tagSchemaInfoList) {
            if (!schemas.contains(info.getSchema())) {
                continue;
            }
            long tagId = info.getTagId();
            if (set.add(tagId)){
                TagInfo tagInfo = tagInfoMap.get(tagId);
                if (tagInfo.getAnalyticsMode() == AnalyticsMode.LOGIC){
                    results.add(tagInfo);
                }
            }
        }
        return results;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public Map<Long, TagInfo> getTagInfoMap() {
        return tagInfoMap;
    }

    public void setTagInfoMap(Map<Long, TagInfo> tagInfoMap) {
        if (tagInfoMap == null){
            return;
        }
        this.tagInfoMap.clear();
        this.tagInfoMap.putAll(tagInfoMap);
    }

    public List<TagSchemaInfo> getTagSchemaInfoList() {
        return tagSchemaInfoList;
    }

    public void setTagSchemaInfoList(List<TagSchemaInfo> tagSchemaInfoList) {
        if (tagSchemaInfoList == null){
            return;
        }
        this.tagSchemaInfoList.clear();
        this.tagSchemaInfoList.addAll(tagSchemaInfoList);
    }

    public List<TagDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<TagDependency> dependencies) {
        if (dependencies == null){
            return;
        }
        this.dependencies.clear();
        this.dependencies.addAll(dependencies);
    }

    public Map<Long, TagParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Long, TagParameter> parameters) {
        if (parameters == null){
            return;
        }
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }
}
