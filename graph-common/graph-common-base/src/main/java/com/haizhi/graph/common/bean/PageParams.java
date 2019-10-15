package com.haizhi.graph.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/5/3.
 */
public class PageParams implements Serializable {

    protected int pageNo = 1;
    protected int pageSize = 10;
    protected List<SortOrder> sort = new ArrayList<>();

    public void addSortOrder(Direction direction, String property){
        this.sort.add(new SortOrder(direction, property));
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        if (pageNo < 1){
            return;
        }
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 0){
            return;
        }
        this.pageSize = pageSize;
    }

    public List<SortOrder> getSort() {
        return sort;
    }

    public void setSort(List<SortOrder> sort) {
        this.sort = sort;
    }

    public static class SortOrder {
        private Direction direction;
        private String property;

        public SortOrder(Direction direction, String property) {
            this.direction = direction;
            this.property = property;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    public enum Direction {
        ASC,
        DESC
    }
}
