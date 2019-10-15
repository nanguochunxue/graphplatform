package com.haizhi.graph.server.api.hbase.query.bean;

import com.haizhi.graph.server.api.hbase.admin.bean.ColumnType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengmo on 2018/5/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aggregation {

    private String key;
    private String column;
    private ColumnType columnType;
    private Stats stats;
}
