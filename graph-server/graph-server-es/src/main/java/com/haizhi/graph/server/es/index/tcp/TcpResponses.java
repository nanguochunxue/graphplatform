package com.haizhi.graph.server.es.index.tcp;

import com.haizhi.graph.common.model.CudResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/11/21.
 */
public class TcpResponses {

    private static final int MAX_BULK_ERROR_MESSAGES = 5;

    public static CudResponse processBulkResponse(BulkResponse resp, CudResponse cudResponse) {
        int rowsAffected = 0;
        int rowsErrors = 0;
        int errorMessagesSoFar = 0;
        List<String> errorMessageSample = new ArrayList<>(MAX_BULK_ERROR_MESSAGES);
        for (BulkItemResponse itemResponse : resp.getItems()) {
            if (!itemResponse.isFailed()){
                rowsAffected++;
                continue;
            }
            rowsErrors++;
            if (errorMessagesSoFar < MAX_BULK_ERROR_MESSAGES) {
                errorMessageSample.add(itemResponse.getFailureMessage());
                errorMessagesSoFar++;
            }
        }
        cudResponse.setResult(rowsAffected, rowsErrors);
        return cudResponse;
    }
}
