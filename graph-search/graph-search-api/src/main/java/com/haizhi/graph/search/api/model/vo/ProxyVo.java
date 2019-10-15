package com.haizhi.graph.search.api.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/7/2.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "代理搜索ProxyVo", description = "代理搜索")
public class ProxyVo {

    @ApiModelProperty(value = "代理搜索结果")
    private Map<String, Object> data;

    public static ProxyVo create(Map<String, Object> esProxyResult){
        return new ProxyVo(esProxyResult);
    }
}
