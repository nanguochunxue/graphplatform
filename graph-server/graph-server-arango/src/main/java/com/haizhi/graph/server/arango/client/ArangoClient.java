package com.haizhi.graph.server.arango.client;

import com.arangodb.ArangoDB;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/1/18.
 */
@Repository
public class ArangoClient {

    private static final GLog LOG = LogFactory.getLogger(ArangoClient.class);

    private static final Map<StoreURL, ArangoDB> POOL = new ConcurrentHashMap<>();

    private ExecutorService asyncExecutor;

    @Value("${arangodb.async.traverse.pool.size:10}")
    private Integer poolSize;

    @PostConstruct
    public void postConstruct() {
        asyncExecutor = Executors.newFixedThreadPool(poolSize == null ? 10 : poolSize);
    }

    @PreDestroy
    public void cleanup() {
        asyncExecutor.shutdown();
        boolean termination = false;
        while (!asyncExecutor.isTerminated()) {
            try {
                termination = asyncExecutor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {
            }
        }
        if (!termination) {
            LOG.warn("force to shutdown asyncExecutor after {0} ms",
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
            asyncExecutor.shutdownNow();
        } else LOG.info("gracefully shutdown asyncExecutor");
    }

    public ArangoDB getClient() {
        return null;
    }

    public ArangoDB getClient(StoreURL storeURL) {
        try {
            ArangoDB client = POOL.get(storeURL);
            if (client == null){
                client = create(storeURL);
                POOL.put(storeURL, client);
            }
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public void testConnect(String url, String user, String password) {
        ArangoDB client = create(url, user, password);
        try {
            client.getVersion();
        } finally {
            client.shutdown();
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private ArangoDB create(@NonNull StoreURL storeURL) {
        return create(storeURL.getUrl(), storeURL.getUser(), storeURL.getPassword());
    }

    private ArangoDB create(@NonNull String url, String user, String password) {
        ArangoDB.Builder builder = new ArangoDB.Builder();
        String[] hostArray = url.split(",");
        for (String host : hostArray) {
            String[] ipPort = host.split(":");
            builder.host(ipPort[0], Integer.parseInt(ipPort[1]));
        }
        ArangoDB client = builder.user(user).password(password).build();
        LOG.info("success to create client of atlas server url[{0}]", url);
        return client;
    }
}