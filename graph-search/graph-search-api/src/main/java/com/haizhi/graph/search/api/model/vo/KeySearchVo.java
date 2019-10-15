package com.haizhi.graph.search.api.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/1/21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "主键搜索KeySearchVo", description = "主键搜索")
public class KeySearchVo {

    @ApiModelProperty(value = "返回结果数据")
    private Map<String, Object> data;

    public static KeySearchVo empty() {
        return new KeySearchVo();
    }

    public static KeySearchVo create(Map<String, Object> data) {
        return new KeySearchVo(data);
    }

}
