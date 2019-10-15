package com.haizhi.graph.search.api.model.vo;

import com.haizhi.graph.server.api.es.search.EsQueryResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2019/4/23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "通用搜素SearchVo", description = "通用搜素")
public class SearchVo {

    @ApiModelProperty(value = "结果数量(条数)")
    private long total;

    @ApiModelProperty(value = "数据集")
    private List<Map<String, Object>> rows;

    @ApiModelProperty(value = "聚合结果")
    private Map<String, Object> aggData;

    public SearchVo(EsQueryResult esQueryResult){
        this.total = esQueryResult.getTotal();
        this.aggData = esQueryResult.getAggData();
        this.rows = esQueryResult.getRows();
    }

    public static SearchVo create(EsQueryResult esQueryResult){
        return new SearchVo(esQueryResult);
    }
}
