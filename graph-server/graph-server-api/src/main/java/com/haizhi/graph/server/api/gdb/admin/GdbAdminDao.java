package com.haizhi.graph.server.api.gdb.admin;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;

/**
 * Created by chengmo on 2019/5/10.
 */
public interface GdbAdminDao {

    boolean existsDatabase(StoreURL storeURL, String database);

    boolean existsTable(StoreURL storeURL, String database, String table);

    boolean createDatabase(StoreURL storeURL, String database);

    boolean createTable(StoreURL storeURL, GdbSuo suo);

    boolean deleteTable(StoreURL storeURL, GdbSuo suo);

    CudResponse bulkPersist(StoreURL storeURL, GdbSuo cuo);

    CudResponse delete(StoreURL storeURL, GdbSuo cuo);
}
