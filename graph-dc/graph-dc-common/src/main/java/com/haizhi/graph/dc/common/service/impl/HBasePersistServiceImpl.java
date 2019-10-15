package com.haizhi.graph.dc.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.monitor.MonitorService;
import com.haizhi.graph.dc.common.service.HBasePersistService;
import com.haizhi.graph.dc.common.util.ValidateUtils;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/5/14.
 */
@Service
public class HBasePersistServiceImpl implements HBasePersistService {

    private static final GLog LOG = LogFactory.getLogger(HBasePersistServiceImpl.class);

    private static Cache<String, Boolean> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private HBaseAdminDao hBaseAdminDao;

    @Autowired
    private MonitorService monitorService;

    @Override
    public CudResponse bulkPersist(DcInboundDataSuo suo) {
        String database = suo.getGraph();
        String table = suo.getSchema();
        CudResponse cudResponse = new CudResponse(database, table);

        // check
        Domain domain = dcMetadataCache.getDomain(database);
        if (!ValidateUtils.checkDomain(domain, suo, cudResponse)){
            LOG.warn(JSON.toJSONString(cudResponse));
            return cudResponse;
        }
        StoreURL storeURL = storeUsageService.findStoreURL(database, StoreType.Hbase);
        if (!ValidateUtils.checkStoreURL(storeURL, StoreType.Hbase, suo, cudResponse)){
            LOG.warn(JSON.toJSONString(cudResponse));
            return cudResponse;
        }

        if (!createDbIfNotExist(storeURL, database)) {
            cudResponse.setRowsErrors(suo.getRows().size());
            cudResponse.setMessage("cannot create db[" + database + "]");
            return cudResponse;
        }
        if (!createTableIfNotExist(storeURL, database, table)) {
            cudResponse.setRowsErrors(suo.getRows().size());
            cudResponse.setMessage("cannot create table[" + database + "/" + table + "]");
            return cudResponse;
        }

        // persist
        switch (suo.getOperation()) {
            case CREATE:
            case UPDATE:
            case CREATE_OR_UPDATE:
                HBaseRows rows = new HBaseRows();
                rows.addRows(suo.getRows());
                cudResponse = hBaseAdminDao.bulkUpsert(storeURL, database, table, rows);
                break;
            case DELETE:
                Set<String> rowKeys = getRowKeys(suo.getRows());
                cudResponse = hBaseAdminDao.deleteByRowKeys(storeURL, database, table, rowKeys);
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
    private Set<String> getRowKeys(List<Map<String, Object>> dataList) {
        Set<String> set = new HashSet<>();
        for (Map<String, Object> row : dataList) {
            String objectKey = Getter.get(Keys.OBJECT_KEY, row);
            String rowKey = KeyFactory.createKeyGetter().getRowKey(objectKey);
            if (StringUtils.isBlank(rowKey)) {
                continue;
            }
            set.add(rowKey);
        }
        return set;
    }

    private boolean createDbIfNotExist(StoreURL storeURL, String database) {
        if (containsCacheKey(database)) {
            return true;
        }
        if (hBaseAdminDao.existsDatabase(storeURL, database)) {
            CACHE.put(database, true);
            return true;
        }
        if (hBaseAdminDao.createDatabase(storeURL, database)) {
            CACHE.put(database, true);
            return true;
        }
        return false;
    }

    private boolean createTableIfNotExist(StoreURL storeURL, String database, String table) {
        String key = database + "/" + table;
        if (containsCacheKey(key)) {
            return true;
        }
        if (hBaseAdminDao.existsTable(storeURL, database, table)) {
            CACHE.put(key, true);
            return true;
        }

        if (hBaseAdminDao.createTable(storeURL, database, table, true)) {
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
