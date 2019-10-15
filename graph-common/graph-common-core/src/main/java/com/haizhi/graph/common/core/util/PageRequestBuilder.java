package com.haizhi.graph.common.core.util;

import com.haizhi.graph.common.model.v0.PageQo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/7/23.
 */
public class PageRequestBuilder {

    public static PageRequest create(PageQo pageQo){
        int pageNo = pageQo.getPageNo() - 1;
        int pageSize = pageQo.getPageSize();
        List<Sort.Order> orders = new ArrayList<>();
        for (PageQo.SortOrder order : pageQo.getSortOrders()) {
            Sort.Direction direction = order.getDirection() == PageQo.Direction.ASC ?
                    Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, order.getProperty()));
        }
        PageRequest pageRequest;
        if (orders.isEmpty()) {
            pageRequest = new PageRequest(pageNo, pageSize);
        } else {
            pageRequest = new PageRequest(pageNo, pageSize, new Sort(orders));
        }
        return pageRequest;
    }
}
