package com.haizhi.graph.server.hbase.client;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.exception.HBaseServerException;
import com.haizhi.graph.server.hbase.conf.HBaseConf;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chengmo on 2018/2/5.
 */
@Repository
public class HBaseClient {

    private static final GLog LOG = LogFactory.getLogger(HBaseClient.class);
    private static final Map<StoreURL, Connection> POOL = new ConcurrentHashMap<>();

    @PreDestroy
    public void cleanup() {
        POOL.values().forEach((connection) -> close(connection));
    }

    /**
     * url=192.168.1.16,192.168.1.17,192.168.1.18:2181
     *
     * @param storeURL
     * @return
     */
    public Connection getConnection(StoreURL storeURL) {
        Connection conn = POOL.get(storeURL);
        if (conn != null && !conn.isClosed()) {
            return conn;
        }
        conn = create(storeURL);
        POOL.put(storeURL, conn);
        return conn;
    }

    public void testConnect(StoreURL storeURL) {
        try (Connection conn = create(storeURL, 10000);
             Admin admin = conn.getAdmin()) {
            LOG.info("hbase namespace length={0}", admin.listNamespaceDescriptors().length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String autoGenUrl(StoreURL storeURL) {
        Configuration conf = HBaseConfiguration.create();
        HBaseConf.addResources(storeURL.getFilePath(), conf);
        String host = conf.get(HConstants.ZOOKEEPER_QUORUM);
        String port = conf.get(HConstants.ZOOKEEPER_CLIENT_PORT);
        if (Strings.isEmpty(host) | Strings.isEmpty(port)) {
            return null;
        }
        return host + ":" + port;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private Connection create(StoreURL storeURL) {
        return create(storeURL, 30000);
    }

    private Connection create(StoreURL storeURL, int operateTimeout) {
        Connection conn = null;
        String url = storeURL.getUrl();
        try {
            if (StringUtils.isBlank(url)) {
                return conn;
            }
            if (StringUtils.contains(url, "://")) {
                url = url.split("://")[1];
            }
            String hosts = StringUtils.substringBefore(url, ":");
            String port = StringUtils.substringAfter(url, ":");
            Configuration conf = HBaseConf.create(storeURL);
            conf.set(HConstants.ZOOKEEPER_QUORUM, hosts);
            conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, port);
            conf.setInt(HConstants.HBASE_RPC_TIMEOUT_KEY, 200000);
            conf.setInt(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 200000);
            conf.setInt(HConstants.HBASE_CLIENT_OPERATION_TIMEOUT, operateTimeout);
            conn = ConnectionFactory.createConnection(conf);
            LOG.info("Success to create connection with HBase server url[{0}]", url);
        } catch (Exception e) {
            LOG.error("Failed to create connection, storeURL={0}", JSON.toJSONString(storeURL));
            throw new HBaseServerException(e);
        }
        return conn;
    }

    private void close(Connection conn) {
        try {
            if (conn == null) {
                return;
            }
            conn.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
