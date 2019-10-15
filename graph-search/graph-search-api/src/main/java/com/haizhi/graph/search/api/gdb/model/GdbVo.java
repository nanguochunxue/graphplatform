package com.haizhi.graph.search.api.gdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengmo on 2019/1/21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GdbVo {
    private Object data;
    private Object aggData;

    public static GdbVo create(Object data, Object aggData){
        return new GdbVo(data, aggData);
    }

    public static GdbVo empty(){
        return new GdbVo();
    }
}
