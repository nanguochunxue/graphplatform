package com.haizhi.graph.server.es.client;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.exception.EsServerException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chengmo on 2018/11/5.
 */
@Repository("esRestClient")
public class EsRestClient {

    private static final GLog LOG = LogFactory.getLogger(EsClient.class);

    private static final int DEFAULT_HTTP_PORT = 9200;
    private static final Map<StoreURL, RestClient> POOL = new ConcurrentHashMap<>();

    @PreDestroy
    public void cleanup() {
        POOL.values().forEach(restClient -> {
            try {
                restClient.close();
            } catch (IOException e) {
                LOG.warn("close client got exception ", e);
            }
        });
    }

    /**
     * Get or create TCP client.
     *
     * @param storeURL url 192.168.1.49:9300,192.168.1.51:9300,192.168.1.52:9300
     * @return
     */
    public RestClient getClient(StoreURL storeURL) {
        try {
            RestClient client = POOL.get(storeURL);
            if (client == null) {
                client = create(storeURL);
                POOL.put(storeURL, client);
            }
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private RestClient create(String url) {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl(url);
        return create(storeURL);
    }

    private RestClient create(StoreURL storeURL) {
        RestClient restClient = null;
        String url = storeURL.getUrl();
        try {
            String[] hosts = url.split(",");
            List<HttpHost> httpHosts = new ArrayList<>();
            for (String hostPort : hosts) {
                String host = StringUtils.substringBefore(hostPort, ":");
                // compatible with tcp port and http port, but web ui not support ,so use DEFAULT_HTTP_PORT
                // int port = Integer.parseInt(StringUtils.substringAfter(url, ":"));
                httpHosts.add(new HttpHost(host, DEFAULT_HTTP_PORT));
            }
            restClient = RestClient.builder(httpHosts.toArray(new HttpHost[]{})).build();
            LOG.info("success to create elasticsearch rest client url[{0}]", url);
        } catch (Exception e) {
            LOG.error("failed to create elasticsearch rest client url[{0}].\n", e, url);
            throw new EsServerException(e);
        }
        return restClient;
    }

}