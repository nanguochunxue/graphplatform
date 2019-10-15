package com.haizhi.graph.dc.core.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengmo on 2018/10/17.
 */
@Data
@ApiModel(value = "元数据-资源库新增或更新参数DcGraphSuo", description = "资源库新增或更新")
public class DcGraphSuo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "资源库名称", example = "graph_one", required = true)
    private String graph;

    @ApiModelProperty(value = "资源库中文名称", example = "图名称", required = true)
    private String graphNameCn;

    @ApiModelProperty(value = "资源库备注", example = "remark")
    private String remark;

    @ApiModelProperty(value = "hbase地址", example = "1", required = true)
    private Long hbase;

    @ApiModelProperty(value = "es地址", example = "2", required = true)
    private Long es;

    @ApiModelProperty(value = "gdb地址", example = "3", required = true)
    private Long gdb;
}
