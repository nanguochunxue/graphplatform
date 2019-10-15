package com.haizhi.graph.dc.arango.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.arango.service.ArangoPersistService;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.util.ValidateUtils;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/11/14.
 */
@Service
public class ArangoPersistServiceImpl implements ArangoPersistService {
    private static final GLog LOG = LogFactory.getLogger(ArangoPersistServiceImpl.class);

    private static Cache<String, Boolean> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private GdbAdminDao gdbAdminDao;

    @Autowired
    private StoreUsageService storeUsageService;

    @Override
    @SuppressWarnings("all")
    public CudResponse bulkPersist(DcInboundDataSuo suo) {
        String database = suo.getGraph();
        String table = suo.getSchema();
        CudResponse cudResponse = new CudResponse(database, table);

        // check
        Domain domain = dcMetadataCache.getDomain(database);
        if (!ValidateUtils.checkDomain(domain, suo, cudResponse)){
            LOG.audit(JSON.toJSONString(cudResponse));
            return cudResponse;
        }
        StoreURL storeURL = storeUsageService.findStoreURL(database, StoreType.GDB);
        if (!ValidateUtils.checkStoreURL(storeURL, StoreType.GDB, suo, cudResponse)){
            LOG.audit(JSON.toJSONString(cudResponse));
            return cudResponse;
        }

        if (!createDbIfNotExist(storeURL, database)) {
            cudResponse.setRowsErrors(suo.getRows().size());
            LOG.error("create gdb db failed: {0}", database);
            cudResponse.setMessage("cannot create db[" + database + "]");
            return cudResponse;
        }

        GdbSuo gdbSuo = new GdbSuo();
        gdbSuo.setGraph(suo.getGraph());
        gdbSuo.setSchema(suo.getSchema());
        gdbSuo.setOperation(suo.getOperation());
        gdbSuo.setType(domain.getSchema(table).getType());

        if (!createTableIfNotExist(storeURL, gdbSuo)) {
            cudResponse.setRowsErrors(suo.getRows().size());
            LOG.error("create gdb table failed: {0}/{1}", database, table);
            cudResponse.setMessage("cannot create table[" + database + "/" + table + "]");
            return cudResponse;
        }

        // persist
        gdbSuo.setRows(suo.getRows());
        GOperation operation = suo.getOperation();
        switch (operation) {
            case CREATE:
            case UPDATE:
            case CREATE_OR_UPDATE:
                cudResponse = gdbAdminDao.bulkPersist(storeURL, gdbSuo);
                break;
            case DELETE:
                cudResponse = gdbAdminDao.delete(storeURL, gdbSuo);
                break;
            default:
                cudResponse.setMessage("No operation specified in (CREATE,UPDATE,DELETE)");
                LOG.error(cudResponse.getMessage());
        }
        return cudResponse;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private boolean createDbIfNotExist(StoreURL storeURL, String database) {
        if (containsCacheKey(database)) {
            return true;
        }
        if (gdbAdminDao.existsDatabase(storeURL, database)) {
            CACHE.put(database, true);
            return true;
        }
        if (gdbAdminDao.createDatabase(storeURL, database)) {
            CACHE.put(database, true);
            return true;
        }
        return false;
    }

    private boolean createTableIfNotExist(StoreURL storeURL, GdbSuo suo) {
        String key = suo.getGraph() + "/" + suo.getSchema();
        if (containsCacheKey(key)) {
            return true;
        }
        if (gdbAdminDao.existsTable(storeURL, suo.getGraph(), suo.getSchema())) {
            CACHE.put(key, true);
            return true;
        }
        if (gdbAdminDao.createTable(storeURL, suo)) {
            CACHE.put(key, true);
            return true;
        }
        return false;
    }

    private boolean containsCacheKey(String key) {
        Boolean value = CACHE.getIfPresent(key);
        return value == null ? false : value;
    }
}