package com.haizhi.graph.tag.analytics.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by chengmo on 2018/4/23.
 */
public class Partitions implements Serializable {

    private Map<String, Partition> partitions = new LinkedHashMap<>();

    public boolean isEmpty(){
        return partitions.isEmpty();
    }

    public void addPartition(String name, String... values){
        if (values.length == 0){
            return;
        }
        Partition p = partitions.get(name);
        if (p == null){
            p = new Partition();
            partitions.put(name, p);
        }
        p.setName(name);
        p.getValues().addAll(Arrays.asList(values));
    }

    public void addRangePartition(String name, String fromRangeValue, String toRangeValue){
        Partition p = partitions.get(name);
        if (p == null){
            p = new Partition();
            partitions.put(name, p);
        }
        p.setName(name);
        p.setFromRangeValue(fromRangeValue);
        p.setToRangeValue(toRangeValue);
    }

    public void addPartitions(Map<String, List<String>> partitions){
        if (partitions == null || partitions.isEmpty()){
            return;
        }
        for (Map.Entry<String, List<String>> entry : partitions.entrySet()) {
            this.addPartition(entry.getKey(), entry.getValue());
        }
    }

    public void addPartition(String name, Collection<String> values){
        if (values == null || values.isEmpty()){
            return;
        }
        Partition p = partitions.get(name);
        if (p == null){
            p = new Partition();
            partitions.put(name, p);
        }
        p.setName(name);
        p.getValues().addAll(values);
    }

    public Map<String, Partition> getPartitions() {
        return partitions;
    }

    public void setPartitions(Map<String, Partition> partitions) {
        if (partitions == null){
            return;
        }
        this.partitions = partitions;
    }

    public static class Partition implements Serializable {
        private String name;
        private Set<String> values = new LinkedHashSet<>();
        private String fromRangeValue;
        private String toRangeValue;

        public boolean isRange(){
            return StringUtils.isNotBlank(fromRangeValue) || StringUtils.isNotBlank(toRangeValue);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<String> getValues() {
            return values;
        }

        public void setValues(Set<String> values) {
            if (values == null){
                return;
            }
            this.values = values;
        }

        public String getFromRangeValue() {
            return fromRangeValue;
        }

        public void setFromRangeValue(String fromRangeValue) {
            this.fromRangeValue = fromRangeValue;
        }

        public String getToRangeValue() {
            return toRangeValue;
        }

        public void setToRangeValue(String toRangeValue) {
            this.toRangeValue = toRangeValue;
        }
    }
}
