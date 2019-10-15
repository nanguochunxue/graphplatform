package com.haizhi.graph.server.api.gdb.admin.model;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.SchemaType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2019/3/5.
 */
@Data
public class GdbSuo {
    private String graph;
    private String schema;
    private SchemaType type;
    private GOperation operation;
    private Map<String, FieldType> fields;
    private List<Map<String, Object>> rows;
}
