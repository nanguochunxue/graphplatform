package com.haizhi.graph.dc.tiger.service.impl;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.dc.tiger.service.TigerPersistService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanghaiyang on 2018/11/14.
 */
@Service
public class TigerPersistServiceImpl implements TigerPersistService {

    private static final GLog LOG = LogFactory.getLogger(TigerPersistServiceImpl.class);

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private GdbAdminDao gdbAdminDao;

    @Autowired
    private StoreUsageService storeUsageService;

    @Override
    @SuppressWarnings("all")
    public CudResponse bulkPersist(DcInboundDataSuo dcInboundDataSuo) {
        String graph = dcInboundDataSuo.getGraph();
        String tableName = dcInboundDataSuo.getSchema();
        CudResponse cudResponse = new CudResponse(graph, tableName);

        Domain domain = dcMetadataCache.getDomain(graph);
        if (domain.invalid()) {
            cudResponse.setMessage("graph not found");
            LOG.warn("graph[{0}] not found", graph);
            return cudResponse;
        }
        StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.Hbase);
        String url = domain.getStoreUrl(StoreType.GDB.name());
        GOperation operation = dcInboundDataSuo.getOperation();
        GdbSuo gdbSuo = buildGdbData(dcInboundDataSuo);

        if (createDbIfNotExist(storeURL, gdbSuo)) {
            cudResponse.setMessage("db or schema is not exist and create it !");
        }
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
    private GdbSuo buildGdbData(DcInboundDataSuo cuo) {
        String graph = cuo.getGraph();
        String schemaName = cuo.getSchema();
        GdbSuo suo = new GdbSuo();
        suo.setGraph(graph);
        suo.setSchema(schemaName);
        suo.setRows(cuo.getRows());
        suo.setOperation(cuo.getOperation());

        Domain domain = dcMetadataCache.getDomain(graph);
        Map<String, Schema> schemaMap = domain.getSchemaMap();
        Schema schema = schemaMap.get(schemaName);

        SchemaType schemaType = schema.getType();
        suo.setType(schemaType);

        Map<String, SchemaField> fieldMap = schema.getFieldMap();
        Set<String> sets = fieldMap.keySet();
        Map<String, FieldType> fields = new HashMap<>();
        for(String key: sets){
            SchemaField schemaField = fieldMap.get(key);
            FieldType fieldType = schemaField.getType();
            String field = schemaField.getField();
            fields.put(field, fieldType);
        }
        suo.setFields(fields);
        return suo;
    }

    private boolean createDbIfNotExist(StoreURL storeURL, GdbSuo gdbSuo){
        String dbName = gdbSuo.getGraph();
        String tableName = gdbSuo.getSchema();
        if (!gdbAdminDao.existsDatabase(storeURL, dbName)){
            if (!gdbAdminDao.createDatabase(storeURL, dbName)) {
                return true;
            }
        }
        if (!gdbAdminDao.existsTable(storeURL, dbName, tableName)){
            if (!gdbAdminDao.createTable(storeURL, gdbSuo)) {
                return true;
            }
        }
        return false;
    }

}
