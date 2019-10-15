package com.haizhi.graph.dc.common.monitor;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;

/**
 * Created by chengangxiong on 2019/03/12
 */
public interface MonitorService {

    void metricStore(DcInboundDataSuo cuo, CudResponse cudResponse, StoreType es);

    void metricTotal(DcInboundDataSuo cuo);

    void errorRecord(DcInboundErrorInfo errorInfo);
}
