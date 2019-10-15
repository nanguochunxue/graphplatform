package com.haizhi.graph.dc.es.consumer;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.es.service.ErrorInfoService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import lombok.Data;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengangxiong on 2019/03/18
 */
@Component
public class ErrorInfoConsumer {

    private static final GLog LOG = LogFactory.getLogger(ErrorInfoConsumer.class);

    public static final String msgCollFamily = "error_col";
    public static final String msgColl = "msg";

    private Map<TableCacheKey, BufferedMutator> tableCache = new HashMap<>();

    @Autowired
    private HBaseAdminDao adminDAO;

    @Autowired
    private ErrorInfoService errorInfoService;

    private ScheduledExecutorService pool;

    @PostConstruct
    public void postConstruct() {
        pool = Executors.newScheduledThreadPool(1);
        pool.scheduleWithFixedDelay(this::flushBuffer, 5, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void preDestroy() {
        closeBuffer();
        pool.shutdownNow();
    }

    @KafkaListener(topicPattern = "${graph.dc.inbound.error.topic}", containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        ack.acknowledge();
        errorInfoService.recordInfo(records);
    }

    private void createTableIfExists(String database, String table, StoreURL storeURL) {
        if (!adminDAO.existsDatabase(storeURL, database)) {
            adminDAO.createDatabase(storeURL, database);
        }
        if (!adminDAO.existsTable(storeURL,database, table)) {
            adminDAO.createTable(storeURL, database, table, Lists.newArrayList(msgCollFamily), true);
        }
    }

    @Data
    public class TableCacheKey {

        private String database;
        private String table;
        private String url;

        public TableCacheKey(String database, String table, String url) {
            this.database = database;
            this.table = table;
            this.url = url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TableCacheKey that = (TableCacheKey) o;
            return Objects.equal(database, that.database) &&
                    Objects.equal(table, that.table) &&
                    Objects.equal(url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(database, table, url);
        }

    }

    private void flushBuffer() {
        tableCache.values().forEach(bufferedMutator -> {
            try {
                bufferedMutator.flush();
            } catch (IOException e) {
                LOG.warn(e);
            }
        });
    }

    private void closeBuffer() {
        tableCache.values().forEach(bufferedMutator -> {
            try {
                bufferedMutator.close();
            } catch (IOException e) {
                LOG.warn(e);
            }
        });
    }
}
