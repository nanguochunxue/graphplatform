package com.haizhi.graph.server.api.hbase.admin;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;

import java.util.List;
import java.util.Set;

/**
 * Created by chengmo on 2018/3/22.
 */
public interface HBaseAdminDao {

    boolean existsDatabase(StoreURL storeURL, String database);

    boolean existsTable(StoreURL storeURL,String database, String table);

    boolean createDatabase(StoreURL storeURL, String database);

    boolean createTable(StoreURL storeURL, String database, String table, boolean preBuildRegion);

    boolean createTable(StoreURL storeURL, String database, String table, List<String> families, boolean preBuildRegion);

    boolean deleteTable(StoreURL storeURL, String database, String table);

    boolean addCoprocessor(StoreURL storeURL, String database, String table, String className);

    boolean addAggCoprocessor(StoreURL storeURL, String database, String table);

    CudResponse bulkUpsert(StoreURL storeURL, String database, String table,  HBaseRows rows);

    CudResponse deleteByRowKeys(StoreURL storeURL, String database, String table,  Set<String> rowKeys);

    CudResponse deleteByScan(StoreURL storeURL, String database, String table, String startRow, String stopRow);

    List<String> listTableNames(StoreURL storeURL,String regex);

}
