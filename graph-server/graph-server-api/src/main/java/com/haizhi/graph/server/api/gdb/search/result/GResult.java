package com.haizhi.graph.server.api.gdb.search.result;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/7.
 */
@Data
public class GResult {
    private List<Map<String, Object>> vertices;
    private List<Map<String, Object>> edges;
}
