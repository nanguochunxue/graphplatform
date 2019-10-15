package com.haizhi.graph.search.api.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengmo on 2019/4/23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "原生搜素NativeSearchVo", description = "原生搜素")
public class NativeSearchVo {

    @ApiModelProperty(value = "返回结果")
    private Object data;

    @ApiModelProperty(value = "聚合结果")
    private Object aggData;

    public NativeSearchVo(Object data) {
        this.data = data;
    }

    public static NativeSearchVo create(Object data) {
        return new NativeSearchVo(data);
    }

    public static NativeSearchVo create(Object data, Object aggData) {
        return new NativeSearchVo(data, aggData);
    }

    public static NativeSearchVo empty() {
        return new NativeSearchVo();
    }
}
