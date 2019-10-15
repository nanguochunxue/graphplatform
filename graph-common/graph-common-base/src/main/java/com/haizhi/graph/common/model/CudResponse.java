package com.haizhi.graph.common.model;

import com.haizhi.graph.common.constant.GOperation;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengmo on 2018/10/29.
 */
@Data
@NoArgsConstructor
public class CudResponse {
    private GOperation operation = GOperation.CREATE_OR_UPDATE;
    private String graph;
    private String schema;
    private boolean success;
    private int rowsRead;
    private int rowsAffected;
    private int rowsErrors;
    private int rowsIgnored;
    private long elapsedTime;
    private Object message;

    public CudResponse(String graph, String schema) {
        this.graph = graph;
        this.schema = schema;
    }

    public CudResponse(String graph, String schema, GOperation operation) {
        this.graph = graph;
        this.schema = schema;
        this.operation = operation;
    }

    public void setResult(int rowsAffected, int rowsErrors){
        this.rowsAffected = rowsAffected;
        this.rowsErrors = rowsErrors;
        this.success = rowsRead == rowsAffected;
    }

    public void onFinished(){
        this.success = rowsRead == rowsAffected;
    }

    public void setFailure(Throwable ex){
        this.message = ex.getMessage();
    }
}
