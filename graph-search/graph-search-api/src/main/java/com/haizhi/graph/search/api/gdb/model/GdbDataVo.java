package com.haizhi.graph.search.api.gdb.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2019/4/16.
 */
@Data
public class GdbDataVo {

    private List<Map<String, Object>> vertices;

    private List<Map<String, Object>> edges;
}
