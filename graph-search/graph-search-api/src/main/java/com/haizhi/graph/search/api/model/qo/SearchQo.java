package com.haizhi.graph.search.api.model.qo;

import com.haizhi.graph.common.model.BaseQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2019/4/23.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "通用查询对象SearchQo", description = "通用搜索条件")
public class SearchQo extends BaseQo {

    @ApiModelProperty(value = "图数据库或es索引", required = true)
    private String graph;

    @ApiModelProperty(value = "搜索关键字", required = true)
    private String keyword;

    @ApiModelProperty(value = "分页数量")
    private int pageNo = 0;

    @ApiModelProperty(value = "分页每页大小")
    private int pageSize = 30;

    @ApiModelProperty(value = "查询表集合", required = true)
    private Set<String> schemas;

    @ApiModelProperty(value = "通用搜索扩展选项")
    private Map<String, Object> option;

    @ApiModelProperty(value = "查询字段列表", required = true)
    private List<Map<String, Object>> query;

    @ApiModelProperty(value = "搜索过滤条件")
    private Map<String, Object> filter;

    @ApiModelProperty(value = "排序")
    private List<Map<String, Object>> sort;

    @ApiModelProperty(value = "返回结果聚合方式")
    private List<Map<String, Object>> aggregation;

    @ApiModelProperty(value = "返回字段")
    private Set<String> fields;

    @ApiModelProperty(value = "高亮显示")
    private boolean highlight;

    @ApiModelProperty(value = "查询超时时间，单位：秒", example = "15")
    private int timeout = 15;
}
