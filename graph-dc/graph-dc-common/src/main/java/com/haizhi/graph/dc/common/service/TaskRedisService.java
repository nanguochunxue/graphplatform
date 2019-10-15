package com.haizhi.graph.dc.common.service;

import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;

public interface TaskRedisService {

    boolean overErrorMode(DcInboundDataSuo cuo);

    boolean overErrorMode(Long taskInstanceId, Integer errorMode);

    Integer getErrorCount(Long taskInstanceId);

    Long incrementErrorCount(Long taskInstanceId, long delta);

    boolean setErrorMode(Long taskInstanceId, Integer errorMode);
}
