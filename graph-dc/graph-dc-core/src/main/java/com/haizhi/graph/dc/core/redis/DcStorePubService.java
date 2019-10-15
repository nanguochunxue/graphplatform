package com.haizhi.graph.dc.core.redis;

import lombok.NonNull;

/**
 * Created by chengmo on 2019/6/25.
 */
public interface DcStorePubService {

    boolean publishByStoreId(@NonNull Long storeId);

    boolean publishByEnvId(@NonNull Long envId);

    boolean publishByGraph(@NonNull String graph);
}
