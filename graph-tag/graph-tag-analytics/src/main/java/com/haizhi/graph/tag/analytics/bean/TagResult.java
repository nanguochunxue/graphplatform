package com.haizhi.graph.tag.analytics.bean;

import com.haizhi.graph.tag.core.domain.DataType;

import java.util.Date;

/**
 * Created by chengmo on 2018/3/1.
 */
public class TagResult {

    private long tagId;
    private String objectKey;
    private DataType dataType;
    private Date updateTime;
    private Object value;

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
