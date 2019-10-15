package com.haizhi.graph.server.api.gdb.search;

import java.util.List;

/**
 * Created by chengmo on 2018/1/18.
 */
public class GdbQueryResult {

    private long total;
    private Object data;
    private Object aggData;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean hasData(){
        if (data instanceof List){
            return !((List) data).isEmpty();
        }
        return false;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getAggData() {
        return aggData;
    }

    public void setAggData(Object aggData) {
        this.aggData = aggData;
    }
}
