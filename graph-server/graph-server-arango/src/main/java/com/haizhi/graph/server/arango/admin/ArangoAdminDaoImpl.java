package com.haizhi.graph.server.arango.admin;

import com.alibaba.fastjson.JSON;
import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.*;
import com.arangodb.model.*;
import com.haizhi.graph.common.constant.IndexTypeEnum;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import com.haizhi.graph.server.arango.client.ArangoClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/1/18.
 */

@Service
public class ArangoAdminDaoImpl implements GdbAdminDao {

    private static final GLog LOG = LogFactory.getLogger(ArangoAdminDaoImpl.class);

    @Autowired
    private ArangoClient arangoClient;

    @Override
    public boolean existsDatabase(StoreURL storeURL, String database) {
        try {
            return arangoClient.getClient(storeURL).getDatabases().contains(database);
        } catch (ArangoDBException e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean existsTable(StoreURL storeURL, String database, String table) {
        try {
            ArangoDatabase adb = arangoClient.getClient(storeURL).db(database);
            for (CollectionEntity collectionEntity : adb.getCollections()) {
                if (collectionEntity.getName().equals(table)) {
                    return true;
                }
            }
        } catch (ArangoDBException e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean createDatabase(StoreURL storeURL, String database) {
        try {
            boolean success = arangoClient.getClient(storeURL).createDatabase(database);
            LOG.info("create database [{0}], success={1}", database, success);
        } catch (ArangoDBException e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean createTable(StoreURL storeURL, GdbSuo suo) {
        try {
            String database = suo.getGraph();
            String table = suo.getSchema();
            CollectionCreateOptions options = new CollectionCreateOptions();
            options.numberOfShards(1);
            options.replicationFactor(1);
            options.shardKeys(Keys._KEY);
            if (suo.getType().isVertex()) {
                options.type(CollectionType.DOCUMENT);
            } else {
                options.type(CollectionType.EDGES);
            }

            // create table
            ArangoDatabase adb = arangoClient.getClient(storeURL).db(database);
            CollectionEntity resEntity = adb.createCollection(table, options);
            if (StringUtils.isBlank(resEntity.getId())) {
                LOG.error("Failed to create table[{0}/{1}]", database, table);
                return false;
            }

            // create index
            createIndex(adb, database, table,
                    IndexTypeEnum.HASHINDEX, Arrays.asList(Keys._KEY), new HashIndexOptions());
            LOG.info("success to create table[{0}/{1}]", database, table);
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean deleteTable(StoreURL storeURL, GdbSuo suo) {
        String database = suo.getGraph();
        String table = suo.getSchema();
        try {
            arangoClient.getClient(storeURL).db(database).collection(table).drop();
            LOG.info("success to delete table[{0}/{1}]", database, table);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to delete table[{0}/{1}]\n", e, database, table);
        }
        return false;
    }

    @Override
    public CudResponse bulkPersist(StoreURL storeURL, GdbSuo suo) {
        CudResponse cudResponse = new CudResponse();
        try {
            DocumentImportOptions options = new DocumentImportOptions().waitForSync(true);
            switch (suo.getOperation()) {
                case CREATE:
                case UPDATE:
                case CREATE_OR_UPDATE:
                    options.onDuplicate(DocumentImportOptions.OnDuplicate.update);
                    break;
                default:
                    String message = "bulk persist operation does not support." + suo.getOperation();
                    cudResponse.setMessage(message);
                    LOG.error(message);
                    return cudResponse;
            }
            String graph = suo.getGraph();
            String schema = suo.getSchema();
            List<Map<String, Object>> rows = getRows(suo);

            // import
            ArangoDatabase adb = arangoClient.getClient(storeURL).db(graph);
            ArangoCollection ac = adb.collection(schema);
            DocumentImportEntity retObj = ac.importDocuments(rows, options);

            // response
            cudResponse.setSuccess(retObj.getErrors() == 0);
            cudResponse.setOperation(suo.getOperation());
            cudResponse.setMessage(retObj.getDetails());
            cudResponse.setRowsIgnored(retObj.getIgnored());
            cudResponse.setGraph(suo.getGraph());
            cudResponse.setSchema(suo.getSchema());
            cudResponse.setRowsRead(suo.getRows().size());
            cudResponse.setRowsAffected(retObj.getUpdated() + retObj.getCreated());
            cudResponse.setRowsErrors(retObj.getErrors());
            LOG.audit("cudResponse:\n{0}", JSON.toJSONString(cudResponse,true));
        } catch (Exception e) {
            LOG.error(e);
        }
        return cudResponse;
    }

    @Override
    public CudResponse delete(StoreURL storeURL, GdbSuo suo) {
        CudResponse cudResponse = new CudResponse();
        try {
            switch (suo.getOperation()) {
                case DELETE:
                    break;
                default:
                    String message = "delete operation does not support." + suo.getOperation();
                    cudResponse.setMessage(message);
                    LOG.error(message);
                    return cudResponse;
            }
            String graph = suo.getGraph();
            String schema = suo.getSchema();
            List<String> keys = suo.getRows().stream().map(e -> {
                return (String) e.get(Keys._ROW_KEY);
            }).collect(Collectors.toList());

            // delete
            ArangoDatabase adb = arangoClient.getClient(storeURL).db(graph);
            ArangoCollection ac = adb.collection(schema);
            MultiDocumentEntity retObj = ac.deleteDocuments(keys);

            // response
            cudResponse.setOperation(suo.getOperation());
            cudResponse.setMessage(retObj.getErrors());
            cudResponse.setGraph(suo.getGraph());
            cudResponse.setSchema(suo.getSchema());
            cudResponse.setRowsRead(suo.getRows().size());
        } catch (Exception e) {
            LOG.error(e);
        }
        return cudResponse;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private IndexEntity createIndex(ArangoDatabase adb, String database, String table, IndexTypeEnum indexTypeEnum, Collection<String>
            fields, Object options) {
        IndexEntity indexEntity = null;
        ArangoCollection arangoCollection = adb.collection(table);
        if(Objects.isNull(indexTypeEnum)) {
            LOG.error("indexTypeEnum should not be null!");
            return null;
        }
        try {
            switch (indexTypeEnum){
                case HASHINDEX:
                    indexEntity = arangoCollection.ensureHashIndex(fields, (HashIndexOptions) options);
                    break;
                case SKIPLISTINDEX:
                    indexEntity = arangoCollection.ensureSkiplistIndex(fields, (SkiplistIndexOptions) options);
                    break;
                case FULLTEXTINDEX:
                    indexEntity = arangoCollection.ensureFulltextIndex(fields, (FulltextIndexOptions) options);
                    break;
                case GEOINDEX:
                    indexEntity = arangoCollection.ensureGeoIndex(fields, (GeoIndexOptions) options);
                    break;
                case PERSISTENTINDEX:
                    indexEntity = arangoCollection.ensurePersistentIndex(fields, (PersistentIndexOptions) options);
                    break;
                default:
                    LOG.error("indexTypeEnum:[{0}] is not type of IndexTypeEnum!", indexTypeEnum);
            }
        } catch (ArangoDBException e) {
            LOG.error("create index error on table[{0}/{1}\n", e, database, table);
        }
        return indexEntity;
    }

    private List<Map<String, Object>> getRows(GdbSuo suo) {
        return suo.getRows().stream().map(e -> {
            e.put(Keys._KEY, Getter.get(Keys.OBJECT_KEY, e));
            e.put(Keys._FROM, e.get(Keys.FROM_KEY));
            e.put(Keys._TO, e.get(Keys.TO_KEY));
            return e;
        }).collect(Collectors.toList());
    }
}
