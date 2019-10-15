package com.haizhi.graph.search.api.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengmo on 2019/1/21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "图查询结果GdbSearchVo", description = "图查询结果")
public class GdbSearchVo {

    @ApiModelProperty(value = "返回结果")
    private Object data;

    @ApiModelProperty(value = "聚合结果")
    private Object aggData;

    public static GdbSearchVo create(Object data, Object aggData){
        return new GdbSearchVo(data, aggData);
    }

    public static GdbSearchVo empty(){
        return new GdbSearchVo();
    }
}
