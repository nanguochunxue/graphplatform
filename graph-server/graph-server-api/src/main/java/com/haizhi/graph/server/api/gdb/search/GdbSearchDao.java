package com.haizhi.graph.server.api.gdb.search;

import com.haizhi.graph.server.api.bean.StoreURL;

/**
 * Created by chengmo on 2018/1/18.
 */
public interface GdbSearchDao {

    ///////////////////////
    // New api tiger
    //////////////////////
    GQueryResult search(GQuery gdbQuery);

    GQueryResult search(String graph, String graphSql);

    ///////////////////////
    // TODO: old api arango
    //////////////////////
    GdbQueryResult search(GdbQuery gdbQuery);

    GdbQueryResult searchByGSQL(GdbQuery gdbQuery);

    GdbQueryResult traverse(GdbQuery gdbQuery);

    GdbQueryResult shortestPath(GdbQuery gdbQuery);

    GdbQueryResult findByIds(GdbQuery gdbQuery);

    GdbQueryResult search(StoreURL storeURL, GdbQuery gdbQuery);

    GdbQueryResult searchByGSQL(StoreURL storeURL, GdbQuery gdbQuery);

    GdbQueryResult traverse(StoreURL storeURL, GdbQuery gdbQuery);

    GdbQueryResult shortestPath(StoreURL storeURL, GdbQuery gdbQuery);

    GdbQueryResult findByIds(StoreURL storeURL, GdbQuery gdbQuery);

}
