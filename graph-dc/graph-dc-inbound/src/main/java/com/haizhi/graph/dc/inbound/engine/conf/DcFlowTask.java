package com.haizhi.graph.dc.inbound.engine.conf;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.engine.flow.conf.BaseFlowTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chengmo on 2019/2/11.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DcFlowTask extends BaseFlowTask implements Serializable {
    private String id;
    private Long taskId;
    private Long instanceId;
    private String graph;
    private String schema;
    private TaskType taskType;
    private List<String> source;
    private String inboundApiUrl;
    private GOperation operation;
}
