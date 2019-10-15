package com.haizhi.graph.dc.store.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;

/**
 * Created by chengangxiong on 2019/03/30
 */
public interface ConnectTestService {
    Response<String> process(DcStoreSuo dcStoreSuo);

    Response<String> autoGenUrl(Long envId, StoreType storeType);

    Response<String> gpExportUrl(String exportUrl);
}
