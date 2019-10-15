package com.haizhi.graph.common.model.plugins.etl.gp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * Created by chengmo on 2019/4/15.
 */
@Data
@ApiModel(value = "GP数据库ETL操作参数",description = "用于卸数组件导出文件")
public class EtlGreenPlumQo {

    @ApiModelProperty(value = "gp任务实例ID", required = true)
    private Long taskInstanceId;

    @ApiModelProperty(value = "gp数据库名", required = true)
    private String database;

    @ApiModelProperty(value = "表", required = true)
    private String table;

    @ApiModelProperty(value = "字段列表，使用[,]分割", required = true)
    private String fields;

    @ApiModelProperty(value = "过滤条件，不含where关键字")
    private String filter;

    @ApiModelProperty(value = "超时时间，单位秒")
    private int timeout;

    public String format(String perlScriptParams) {
        if (Objects.nonNull(filter)){
            perlScriptParams += " -table_filt=" + filter;
        }
        perlScriptParams += " -table_name=" + table + " -select_list=" + fields;
        return perlScriptParams;
    }
}
