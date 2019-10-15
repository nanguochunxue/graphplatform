package com.haizhi.graph.dc.core.model.suo;

import lombok.Data;

/**
 * Created by chengangxiong on 2019/02/25
 */
@Data
public class ApiTaskSuo {

    private Long id;

    private Long storeId;

    private Integer totalRows;

    private Integer errorRows;

    private String OperateDt;
}
