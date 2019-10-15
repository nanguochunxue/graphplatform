package com.haizhi.graph.server.api.gdb.search;

import com.haizhi.graph.server.api.bean.StoreURL;

/**
 * Created by chengmo on 2019/3/13.
 */
public class GdbSearchBase implements GdbSearchDao {

    @Override
    public GQueryResult search(GQuery gdbQuery) {
        return new GQueryResult();
    }

    @Override
    public GQueryResult search(String graph, String graphSql) {
        return new GQueryResult();
    }

    @Override
    public GdbQueryResult search(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult searchByGSQL(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult traverse(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult shortestPath(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult findByIds(GdbQuery gdbQuery){
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult search(StoreURL storeURL, GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult searchByGSQL(StoreURL storeURL, GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult traverse(StoreURL storeURL, GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult shortestPath(StoreURL storeURL, GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult findByIds(StoreURL storeURL, GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }
}
