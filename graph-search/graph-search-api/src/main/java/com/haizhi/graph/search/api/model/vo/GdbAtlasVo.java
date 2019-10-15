package com.haizhi.graph.search.api.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengmo on 2018/6/5.
 */
@Data
@ApiModel(value = "图谱查询结果GdbAtlasVo", description = "图谱查询结果")
public class GdbAtlasVo {

    @ApiModelProperty(value = "结果集")
    private Object data;

    @ApiModelProperty(value = "树状结果集")
    private Object treeData;

    public static GdbAtlasVo empty(){
        return new GdbAtlasVo();
    }

}
