package com.haizhi.graph.dc.inbound.api.validator;

import com.google.common.collect.Maps;
import com.haizhi.graph.common.log.GLogBuffer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2019/6/17.
 */
@Data
public class ValidatorResult {
    private GLogBuffer buffer;
    private List<Map<String, Object>> errorRows;
    private boolean success;

    public ValidatorResult(int logBufferSize) {
        this.buffer = new GLogBuffer(logBufferSize > 0 ? logBufferSize : 100);
        this.errorRows = new ArrayList<>();
    }

    public boolean hasErrorRow(){
        return !this.errorRows.isEmpty();
    }

    public boolean hasErrorMsg(){
        return this.buffer.size() > 0;
    }

    public void addErrorMsg(String message){
        this.buffer.addLine(message);
    }

    public void addErrorRow(Map<String, Object> row){
        this.errorRows.add(Maps.newHashMap(row));
    }

    public void addErrorRows(List<Map<String, Object>> rows){
        if (rows == null){
            return;
        }
        this.errorRows.addAll(rows);
    }

    public List<Object> getAllErrorMsg(){
        return this.buffer.getLines();
    }
}
