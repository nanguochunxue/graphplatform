package com.haizhi.graph.common.model.v0;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/5/3.
 */
@Data
public class PageQo implements Serializable {
    protected int pageNo = 1;
    protected int pageSize = 10;
    protected List<String> sort;
    protected List<SortOrder> sortOrders = new ArrayList<>();

    public void addSortOrder(String property, Direction direction){
        this.sortOrders.add(new SortOrder(property, direction));
    }

    public List<SortOrder> getSortOrders(){
        if (sort == null){
            return sortOrders;
        }
        for (String str : sort) {
            Direction direction = Direction.ASC;
            String property = str;
            if (str.contains(",")){
                String[] arr = str.split(",");
                property = arr[0];
                direction = Direction.fromName(arr[1]);
            }
            this.addSortOrder(property, direction);
        }
        return sortOrders;
    }

    @Data
    @AllArgsConstructor
    public static class SortOrder {
        private String property;
        private Direction direction;
    }

    public enum Direction {
        ASC,
        DESC;

        public static Direction fromName(String name){
            try {
                return Direction.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Direction.ASC;
            }
        }
    }
}
