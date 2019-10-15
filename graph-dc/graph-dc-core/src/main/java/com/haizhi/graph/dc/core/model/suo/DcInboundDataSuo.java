package com.haizhi.graph.dc.core.model.suo;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.dc.core.constant.DcInboundType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/10/24.
 */
@Data
@NoArgsConstructor
public class DcInboundDataSuo {
    private Header header = new Header();
    private String graph;
    private String schema;
    private GOperation operation;
    private List<Map<String, Object>> rows;

    public DcInboundDataSuo(String graph, String schema, GOperation operation, List<Map<String, Object>> rows) {
        this.graph = graph;
        this.schema = schema;
        this.operation = operation;
        this.rows = rows;
    }

    public DcInboundDataSuo(String graph, String schema, List<Map<String, Object>> rows) {
        this(graph, schema, GOperation.CREATE_OR_UPDATE, rows);
    }

    public JSONObject getHeaderOptions(){
        return this.header.getOptions();
    }

    public int getRowsSize(){
        if (rows == null || rows.isEmpty()){
            return 0;
        }
        return rows.size();
    }

    @Data
    public static class Header {
        private String user;
        private String password;
        private DcInboundType type = DcInboundType.API;
        private JSONObject options = new JSONObject();

        public void putOption(String key, Object value){
            this.options.put(key, value);
        }

        public void putOptions(Map<String, Object> map){
            this.options.putAll(map);
        }
    }
}
