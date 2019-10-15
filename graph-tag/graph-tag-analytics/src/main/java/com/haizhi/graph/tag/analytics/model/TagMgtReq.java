package com.haizhi.graph.tag.analytics.model;

import lombok.Data;

/**
 * Created by chengmo on 2018/7/25.
 */
@Data
public class TagMgtReq {
    private long tagId;
    private Operation operation;
    private String operateBy;

    public enum Operation{
        APPLY, CANCEL_APPLY, REJECT, UP, DOWN
    }
}
