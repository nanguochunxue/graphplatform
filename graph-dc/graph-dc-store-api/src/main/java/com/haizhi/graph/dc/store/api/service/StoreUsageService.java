package com.haizhi.graph.dc.store.api.service;

import com.haizhi.graph.common.cache.Cacheable;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.server.api.bean.StoreURL;
import lombok.NonNull;

/**
 * Created by chengangxiong on 2019/04/10
 */
public interface StoreUsageService extends Cacheable {

    StoreURL findStoreURL(String graph, StoreType storeType);

    StoreURL findStoreURL(@NonNull Long envId);
}
