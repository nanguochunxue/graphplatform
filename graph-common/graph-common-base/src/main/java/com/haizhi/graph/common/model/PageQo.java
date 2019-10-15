package com.haizhi.graph.common.model;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/5/3.
 */
@Data
@NoArgsConstructor
@ApiModel(value = "分页查询参数",description = "用于指定分页查询相关参数")
public class PageQo implements Serializable {

    @Min(value = 1)
    @ApiModelProperty(value = "当前页",example = "1")
    protected int pageNo = 1;

    @Min(value = 2)
    @ApiModelProperty(value = "每页显示大小",example = "10")
    protected int pageSize = 10;

    @ApiModelProperty(value = "排序字段列表")
    protected List<SortOrder> sortOrders = new ArrayList<>();

    public void addSortOrder(String property, Sort.Direction direction) {
        this.sortOrders.add(new SortOrder(property, direction));
    }

    public PageQo(int pageNo, int pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public List<OrderSpecifier> buildQueryDslOrder(EntityPathBase orderPropertyHolder) {
        if (CollectionUtils.isEmpty(sortOrders) || (null == orderPropertyHolder)){
            return Collections.emptyList();
        }

        List<OrderSpecifier> orderSpecifiers = sortOrders.stream().map(
                sortOrder -> {
                    PathBuilder orderByExpression = new PathBuilder(Object.class, orderPropertyHolder.getMetadata().getName());
                    OrderSpecifier orderSpecifier = new OrderSpecifier(
                            sortOrder.getDirection().isAscending() ? Order.ASC : Order.DESC,
                            orderByExpression.get(sortOrder.getProperty()));
                    return orderSpecifier;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        return orderSpecifiers;
    }
}
