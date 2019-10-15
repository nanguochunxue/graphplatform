package com.haizhi.graph.server.es.client;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chengmo on 2017/12/01.
 */
@Repository("esClient")
public class EsClient {

    private static final GLog LOG = LogFactory.getLogger(EsClient.class);

    private static final Map<StoreURL, TransportClient> POOL = new ConcurrentHashMap<>();

    @PreDestroy
    public void cleanup() {
        POOL.values().forEach(TransportClient::close);
    }

    /**
     * Get or create TCP client.
     *
     * @param storeURL 192.168.1.49:9300,192.168.1.51:9300,192.168.1.52:9300
     * @return
     */
    public TransportClient getClient(StoreURL storeURL) {
        try {
            TransportClient client = POOL.get(storeURL);
            if (client == null){
                client = create(storeURL);
                POOL.put(storeURL, client);
            }
            return client;
        } catch (Exception e) {
            LOG.error("cannot get client [" + storeURL.getUrl() + "]", e);
            throw new RuntimeException(e);
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private TransportClient create(StoreURL storeURL) {
        TransportClient client = null;
        String url = storeURL.getUrl();
        try {
            Settings settings = Settings.builder()
                    .put("client.transport.ignore_cluster_name", true)
                    .build();
            client = new PreBuiltTransportClient(settings);
            String[] hostArray = url.split(",");
            for (String host : hostArray) {
                String[] ipPort = host.split(":");
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipPort[0]), Integer
                        .parseInt(ipPort[1])));
            }
            LOG.info("success to create client of elasticsearch server url[{0}]", url);
            if(client.connectedNodes().size() == 0){
                throw new RuntimeException("the ES client doesn't connect to the ES server url[" + url + "]");
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to create client of elasticsearch server url[" + url + "].\n", e);
        }
        return client;
    }

}