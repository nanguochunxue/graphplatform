package com.haizhi.graph.server.hbase.admin;

import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.KeyUtils;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.key.partition.RowKeyPartitioner;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.Coprocessors;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import com.haizhi.graph.server.hbase.util.BytesBuilder;
import com.haizhi.graph.server.hbase.util.CloseableUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/3/22.
 */
@Repository
public class HBaseAdminDaoImpl implements HBaseAdminDao {

    private static final GLog LOG = LogFactory.getLogger(HBaseAdminDaoImpl.class);

    @Autowired
    private HBaseClient hBaseClient;

    private Connection getConnection(@NonNull StoreURL storeURL) {
        return hBaseClient.getConnection(storeURL);
    }

    @Override
    public boolean existsDatabase(StoreURL storeURL, String database) {
        boolean exists = false;
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            NamespaceDescriptor descriptor = admin.getNamespaceDescriptor(database);
            if (descriptor != null) {
                exists = true;
            }
        } catch (NamespaceNotFoundException ex) {
            // ignore
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            CloseableUtils.close(admin);
        }
        return exists;
    }


    @Override
    public boolean existsTable(StoreURL storeURL, String database, String table) {
        boolean exists = false;
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            exists = admin.tableExists(TableName.valueOf(database, table));
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            CloseableUtils.close(admin);
        }
        return exists;
    }


    @Override
    public boolean createDatabase(StoreURL storeURL, String database) {
        boolean success = false;
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(database).build();
            admin.createNamespace(namespaceDescriptor);
            success = true;
            LOG.info("Success to create database[{0}]", database);
        } catch (IOException e) {
            LOG.error(e);
            LOG.error("Failed to create database[{0}]\n", e, database);
        } finally {
            CloseableUtils.close(admin);
        }
        return success;
    }

    @Override
    public boolean createTable(StoreURL storeURL, String database, String table, boolean preBuildRegion) {
        return this.createTable(storeURL, database, table, Lists.newArrayList(HBaseRows.DEFAULT_FAMILY), preBuildRegion);
    }

    @Override
    public boolean createTable(StoreURL storeURL, String database, String table, List<String> families, boolean
            preBuildRegion) {
        boolean success = false;
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            boolean exists = admin.tableExists(TableName.valueOf(database, table));
            if (exists) {
                LOG.info("Table[{0}:{1}] is already exists.", database, table);
                return true;
            }

            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(database, table));
            for (String family : families) {
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(family);
                columnDescriptor.setMaxVersions(3);
                tableDesc.addFamily(columnDescriptor);
            }

            // create
            if (preBuildRegion) {
                int logicPartitions = KeyUtils.LOGIC_PARTITIONS;
                int physicsPartitions = 16;
                byte[][] splitKeys = RowKeyPartitioner.getSplitKeysBytes(logicPartitions, physicsPartitions);
                admin.createTable(tableDesc, splitKeys);
            } else {
                admin.createTable(tableDesc);
            }
            success = true;
            LOG.info("Success to create table[{0}:{1}]", database, table);
        } catch (IOException e) {
            LOG.error("Failed to create table[{0}/{1}]\n", e, database, table);
        } finally {
            CloseableUtils.close(admin);
        }
        return success;
    }

    @Override
    public boolean deleteTable(StoreURL storeURL, String database, String table) {
        boolean success = false;
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            TableName tableName = TableName.valueOf(database, table);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            success = true;
            LOG.info("Success to delete table[{0}:{1}]", database, table);
        } catch (IOException e) {
            LOG.error("Failed to delete table[{0}/{1}]\n", e, database, table);
        } finally {
            CloseableUtils.close(admin);
        }
        return success;
    }

    @Override
    public boolean addCoprocessor(StoreURL storeURL, String database, String table, String className) {
        boolean success = false;
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            TableName tableName = TableName.valueOf(database, table);

            HTableDescriptor tableDesc = admin.getTableDescriptor(tableName);
            if (tableDesc.hasCoprocessor(className)) {
                LOG.info("Table[{0}:{1}] coprocessor is already exists, {2}", database, table, className);
                return true;
            }
            admin.disableTable(tableName);
            tableDesc.addCoprocessor(className);
            admin.modifyTable(tableName, tableDesc);
            admin.enableTable(tableName);
            success = true;
            LOG.info("Success to add coprocessor table[{0}:{1}], {2}", database, table, className);
        } catch (IOException e) {
            LOG.error("Failed to add coprocessor table[{0}/{1}], {2}\n", e, database, table, className);
        } finally {
            CloseableUtils.close(admin);
        }
        return success;
    }

    @Override
    public boolean addAggCoprocessor(StoreURL storeURL, String database, String table) {
        return this.addCoprocessor(storeURL, database, table, Coprocessors.AGGREGATION);
    }

    @Override
    public CudResponse bulkUpsert(StoreURL storeURL, String database, String table, HBaseRows rows) {
        CudResponse cudResponse = new CudResponse(database, table);
        String tableName = database + ":" + table;
        BufferedMutator mutator = null;
        try {
            // puts
            List<Put> puts = new ArrayList<>();
            String family = rows.getFamily();
            int rowsIgnored = 0;
            cudResponse.setRowsRead(rows.getRows().size());
            for (Map<String, Object> row : rows.getRows()) {
                String objectKey = Getter.get(Keys.OBJECT_KEY, row);
                String rowKey = KeyFactory.createKeyGetter().getRowKey(objectKey);
                if (row.size() <= 1 || StringUtils.isBlank(rowKey)) {
                    rowsIgnored++;
                    continue;
                }
                Put put = new Put(Bytes.toBytes(rowKey));
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String field = entry.getKey();
                    Object value = entry.getValue();
                    if (Keys._ROW_KEY.equals(field)) {
                        continue;
                    }
                    byte[] valueBytes = BytesBuilder.serialize(value);
                    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(field), valueBytes);
                }
                puts.add(put);
            }
            cudResponse.setRowsIgnored(rowsIgnored);
            if (puts.isEmpty()) {
                LOG.warn("Puts is empty.");
                return cudResponse;
            }

            // async update
            BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(database, table));

            // this lister will cause error exception in another thread ,and setRowsAffected will invalid !
            /*params.listener((e, m) -> {
                int numExceptions = e.getNumExceptions();
                cudResponse.setRowsErrors(numExceptions);
                LOG.error("Failed to bulk upsert rows[{0}], caused by:\n{1}", e.getNumExceptions(), e.getMessage());
                for (int i = 0; i < numExceptions; i++) {
                    LOG.error("Failed to bulk upsert row[{0}]", Bytes.toString(e.getRow(i).getRow()));
                }
            });*/
            mutator = this.getConnection(storeURL).getBufferedMutator(params);
            int size = puts.size();
            mutator.mutate(puts);
            mutator.flush();
            cudResponse.setRowsAffected(size);
            LOG.audit("Success to prepare bulk upsert rows[{0}] table[{1}]", size, tableName);
        }catch (Exception e){
            LOG.error("Failed to bulk upsert rows table[{0}]\n", e, tableName);
            cudResponse.setRowsErrors(rows.getRows().size());
        } finally{
            CloseableUtils.close(mutator);
            cudResponse.onFinished();
        }
        return cudResponse;
    }

    @Override
    public CudResponse deleteByRowKeys(StoreURL storeURL, String database, String table, Set<String> rowKeys) {
        CudResponse cudResponse = new CudResponse(database, table);
        cudResponse.setOperation(GOperation.DELETE);
        String tableName = database + ":" + table;
        Table hTable = null;
        try {
            hTable = this.getConnection(storeURL).getTable(TableName.valueOf(tableName));
            List<Delete> deleteList = new ArrayList<>();
            for (String rowKey : rowKeys) {
                Delete delete = new Delete(Bytes.toBytes(rowKey));
                deleteList.add(delete);
            }
            int size = rowKeys.size();
            cudResponse.setRowsRead(size);
            // delete
            if (!deleteList.isEmpty()) {
                hTable.delete(deleteList);
                cudResponse.setRowsAffected(size);
                LOG.audit("Success to delete [{0}] rows.", size);
            }
            hTable.close();
        } catch (Exception e) {
            LOG.error("Failed to delete by rowKeys table[{0}]\n", e, tableName);
        } finally {
            CloseableUtils.close(hTable);
            cudResponse.onFinished();
        }
        return cudResponse;
    }

    @Override
    public CudResponse deleteByScan(StoreURL storeURL, String database, String table, String startRow, String stopRow) {
        CudResponse cudResponse = new CudResponse(database, table);
        cudResponse.setOperation(GOperation.DELETE);
        String tableName = database + ":" + table;
        Table hTable = null;
        try {
            hTable = this.getConnection(storeURL).getTable(TableName.valueOf(database, table));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(stopRow));
            scan.setCaching(2000);
            scan.setCacheBlocks(false);
            //scan.addColumn(Bytes.toBytes(Keys.OBJECTS), Bytes.toBytes(Keys.OBJECT_KEY));

            ResultScanner results = hTable.getScanner(scan);
            List<Delete> deleteList = new ArrayList<>();
            for (Result result : results) {
                Delete delete = new Delete(result.getRow());
                deleteList.add(delete);
            }
            results.close();
            int size = deleteList.size();
            cudResponse.setRowsRead(size);
            // delete
            if (!deleteList.isEmpty()) {
                hTable.delete(deleteList);
                cudResponse.setRowsAffected(size);
                LOG.audit("Success to delete [{0}] rows.", size);
            }
            hTable.close();
        } catch (Exception e) {
            LOG.error("Failed to delete by scan table[{0}]\n", e, tableName);
        } finally {
            CloseableUtils.close(hTable);
            cudResponse.onFinished();
        }
        return cudResponse;
    }

    @Override
    public List<String> listTableNames(StoreURL storeURL, String regex) {
        List<String> results = new ArrayList<>();
        try {
            HBaseAdmin admin = (HBaseAdmin) this.getConnection(storeURL).getAdmin();
            TableName[] tableNames = admin.listTableNames(regex);
            for (TableName tableName : tableNames) {
                // database:collection
                results.add(tableName.getNameAsString());
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return results;
    }

    public BufferedMutator table(String database, String table, StoreURL storeURL) throws IOException {
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(database, table));
        return getConnection(storeURL).getBufferedMutator(params);
    }
}
