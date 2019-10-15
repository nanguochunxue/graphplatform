package com.haizhi.graph.search.api.model.qo;

import com.haizhi.graph.common.model.BaseQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/1/21.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "主键搜素对象KeySearchQo", description = "主键搜索条件")
public class KeySearchQo extends BaseQo {

    @ApiModelProperty(value = "图数据库", required = true)
    private String graph;

    @ApiModelProperty(value = "主键集合", required = true, example = "{ \"graph\":\"test\", \"schemas\": { \"Company\": [ \"344ae\", \"81dc\" ], \"Invest\": [ \"JFK333\", \"JF1111\" ] }}")
    private Map<String, Set<String>> schemaKeys = new LinkedHashMap<>();

}
