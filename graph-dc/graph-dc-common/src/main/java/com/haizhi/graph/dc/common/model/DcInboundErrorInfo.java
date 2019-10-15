package com.haizhi.graph.dc.common.model;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.core.EsBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengangxiong on 2019/03/18
 */
@Data
@NoArgsConstructor
public class DcInboundErrorInfo {

    private String graph;
    private String schema;
    private ErrorType errorType;
    private StoreType storeType;
    private String msg;
    private Long taskId;
    private Long taskInstanceId;
    private List<Map<String, Object>> rows;

    public DcInboundErrorInfo(String graph, String schema, ErrorType errorType) {
        this.graph = graph;
        this.schema = schema;
        this.errorType = errorType;
    }

    public DcInboundErrorInfo(DcInboundResult result, ErrorType errorType) {
        this(result.getCudResponse().getGraph(), result.getCudResponse().getSchema(), errorType);
    }

    public void setMsg(List<Object> lines) {
        this.msg = Joiner.on(" ").join(lines);
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMsg(Object message) {
        this.msg = JSON.toJSONString(message);
    }

    public void setInboundDataSuoInfo(DcInboundDataSuo suo, List<Map<String, Object>> errorRows) {
        this.taskId = suo.getHeaderOptions().getLongValue(DcConstants.KEY_TASK_ID);
        this.taskInstanceId = suo.getHeaderOptions().getLongValue(DcConstants.KEY_TASK_INSTANCE_ID);
        this.rows = errorRows;
    }

    public void setInboundDataSuoInfo(DcInboundDataSuo suo) {
        this.taskId = suo.getHeaderOptions().getLongValue(DcConstants.KEY_TASK_ID);
        this.taskInstanceId = suo.getHeaderOptions().getLongValue(DcConstants.KEY_TASK_INSTANCE_ID);
        this.rows = suo.getRows();
    }

    public enum ErrorType {
        CHECK_ERROR, RUNTIME_ERROR
    }

    public String getRowKey() {
        int crc32 = Hashing.crc32().hashString(msg, Charsets.UTF_8).asInt();
        if (Objects.nonNull(storeType)) {
            return MessageFormat.format("{0}-{1}-{2}-{3}-{4}", graph, schema, errorType, storeType, crc32);
        }
        return MessageFormat.format("{0}-{1}-{2}-{3}", graph, schema, errorType, crc32);
    }

    public static final String HBASE_TABLE_PATTERN = "{0}.{1}.{2}";
    public static final String ES_GRAPH_PATTERN = "{0}.error";
    public static final String ES_TYPE_PATTERN = "{0}_error";

    public String getHBaseTable() {
        return MessageFormat.format(HBASE_TABLE_PATTERN, graph, schema, "error");
    }

    public static class EsHelper {

        public static String getEsGraph(String graph, StoreURL storeURL) {
            if (storeURL.getStoreVersion().startsWith(EsBuilder.V5_X)) {
                return MessageFormat.format(ES_GRAPH_PATTERN, graph).toLowerCase();
            }
            return graph;
        }

        public static String getEsType(String schema) {
            return MessageFormat.format(ES_TYPE_PATTERN, schema).toLowerCase();
        }
    }

}
