package com.haizhi.graph.server.api.gdb.search;

import lombok.Data;

import java.util.List;

/**
 * Created by chengmo on 2019/3/13.
 */
@Data
public class GQueryResult {
    private long total;
    private Object data;
    private Object aggData;

    public boolean hasData(){
        if (data instanceof List){
            return !((List) data).isEmpty();
        }
        return false;
    }
}
