package com.haizhi.graph.dc.common.model;

import com.haizhi.graph.common.model.CudResponse;
import lombok.Data;

/**
 * Created by chengmo on 2018/10/29.
 */
@Data
public class DcInboundResult {
    private CudResponse cudResponse;
    private int retryCount;

    public boolean isSuccess() {
        return cudResponse == null ? false : cudResponse.isSuccess();
    }

    public static DcInboundResult get(CudResponse cudResponse) {
        DcInboundResult result = new DcInboundResult();
        result.setCudResponse(cudResponse);
        return result;
    }
}
