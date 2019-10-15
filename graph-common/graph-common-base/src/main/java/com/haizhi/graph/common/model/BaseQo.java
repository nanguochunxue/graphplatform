package com.haizhi.graph.common.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengmo on 2018/6/22.
 */
@Data
public class BaseQo {
    public static final String DEBUG_FIELD = "debug";
    public static final String TIMEOUT = "timeout";

    @ApiModelProperty(value = "内部可选参数")
    private JSONObject internalOption = new JSONObject();

    public boolean isDebug() {
        return internalOption.getBooleanValue(DEBUG_FIELD);
    }

    public void setDebug(boolean debugEnabled) {
        this.internalOption.put(DEBUG_FIELD, debugEnabled);
    }
}
