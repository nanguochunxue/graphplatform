package com.haizhi.graph.server.api.es.search;

import com.haizhi.graph.server.api.bean.StoreURL;

import java.util.List;

/**
 * Created by chengmo on 2019/4/30.
 */
public interface EsSearchDao {

    EsQueryResult search(StoreURL storeURL, EsQuery esQuery);

    EsQueryResult searchByIds(StoreURL storeURL, EsQuery esQuery);

    EsQueryResult searchByFields(StoreURL storeURL, EsQuery esQuery);

    List<EsQueryResult> multiSearch(StoreURL storeURL, List<EsQuery> list);

    EsQueryResult searchByDSL(StoreURL storeURL, EsQuery esQuery);

    Object executeProxy(StoreURL storeURL, EsProxy esProxy);
}
