package com.haizhi.graph.server.hive;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/16.
 */
public class HiveHelper implements AutoCloseable{

    private static final GLog LOG = LogFactory.getLogger(HiveHelper.class);
    private static final String DRIVER = "org.apache.hive.jdbc.HiveDriver";

    private Connection conn;
    private HiveConfig config;

    public HiveHelper(String locationConfig) {
        this(new HiveConfig(locationConfig));
    }

    public HiveHelper(HiveConfig config) {
        this.config = config;
        this.connect();
    }

    /**
     * Create table with script if it is not exists.
     *
     * @param database
     * @param tableCreateScript
     * @return
     */
    public boolean createTableIfNotExists(String database, String tableCreateScript){
        List<String> sqlList = new ArrayList<>();
        sqlList.add("CREATE DATABASE IF NOT EXISTS " + database);
        sqlList.add("USE " + database);
        sqlList.add(tableCreateScript);
        boolean success = this.execute(sqlList);
        if (success){
            LOG.info("Success to create table if not exists.");
        }
        return success;
    }

    /**
     * @param database
     * @return
     */
    public List<HiveTable> describeTables(String database) {
        List<HiveTable> tables = new ArrayList<>();
        try {
            Statement stmt = this.conn.createStatement();
            if (!StringUtils.isBlank(database)) {
                stmt.execute("use " + database);
            }

            List<String> tableNames = new ArrayList<>();
            ResultSet res = stmt.executeQuery("show tables");
            while (res.next()) {
                tableNames.add(res.getString(1));
            }
            for (String tableName : tableNames) {
                HiveTable table = new HiveTable(tableName);
                res = stmt.executeQuery("describe " + tableName);
                while (res.next()) {
                    String columnName = res.getString(1);
                    String columnType = res.getString(2);
                    if (StringUtils.isAnyBlank(columnName, columnType)
                            || columnName.startsWith("#")) {
                        continue;
                    }
                    table.addField(new HiveTable.Field(columnName, columnType));
                }
                tables.add(table);
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
        return tables;
    }

    /**
     * @param sqlList
     * @return
     */
    public boolean execute(List<String> sqlList) {
        boolean success = true;
        String tempSql = "";
        try {
            Statement stmt = this.conn.createStatement();
            for (String sql : sqlList) {
                tempSql = sql;
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            success = false;
            LOG.error("Execute error with sql:\n{0}", e, tempSql);
        }
        return success;
    }

    /**
     * @param sql
     * @return
     */
    public boolean execute(String sql) {
        boolean success = true;
        try {
            Statement stmt = this.conn.createStatement();
            stmt.execute(sql);
            LOG.info("SQL>>{0}", sql);
        } catch (SQLException e) {
            success = false;
            LOG.error("Execute error with sql:\n{0}", e, sql);
        }
        return success;
    }

    /**
     * @param sql
     * @return
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    row.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            LOG.error("Execute query error with sql:\n{0}", e, sql);
        }
        return resultList;
    }

    public void close() {
        try {
            if (conn == null || conn.isClosed()) {
                return;
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void connect() {
        try {
            if (conn != null && !conn.isClosed()) {
                return;
            }
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(config.getUrl(),
                    config.getUserName(), config.getPassword());
            LOG.info("Success to connect hive jdbc with url:\n{0}", config.getUrl());
        } catch (ClassNotFoundException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOG.error("Failed to connect hive jdbc with url:\n{0}", e,config.getUrl());
            throw new RuntimeException(e);
        }
    }

    public void testConnect() {
        connect();
        try {
            conn.createStatement().executeQuery("show tables");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
