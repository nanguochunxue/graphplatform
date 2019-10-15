package com.haizhi.graph.server.api.hbase.query.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/2/5.
 */
@Data
@NoArgsConstructor
public class TableResult {

    private String tableName;
    private List<Map<String, Object>> rows;

}
