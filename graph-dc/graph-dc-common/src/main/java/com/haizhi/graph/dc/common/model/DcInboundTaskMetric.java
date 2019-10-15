package com.haizhi.graph.dc.common.model;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.constant.DcInboundType;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chengangxiong on 2019/03/09
 */
@Data
public class DcInboundTaskMetric {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private Long taskId;
    private Long taskInstanceId;

    private Long rows;
    private Long errorRows;

    private StoreType storeType;
    private String batchId;
    private DcInboundType inboundType;
    private String dailyStr;

    public DcInboundTaskMetric() {
    }

    public DcInboundTaskMetric(JSONObject headOption) {
        this.taskId = headOption.getLong(DcConstants.KEY_TASK_ID);
        this.taskInstanceId = headOption.getLong(DcConstants.KEY_TASK_INSTANCE_ID);
        this.dailyStr = new SimpleDateFormat(DATE_FORMAT).format(new Date());
    }
}
