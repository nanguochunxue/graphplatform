package com.haizhi.graph.server.es6.client;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.haizhi.graph.common.cache.CacheFactory;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.exception.EsServerException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/11/5.
 */
@Repository
public class EsRestClient {
    private static final GLog LOG = LogFactory.getLogger(EsRestClient.class);

    private static final int CONNECT_TIMEOUT = 15000;   // 15 sec
    private static final int SOCKET_TIMEOUT = 60000;    // 60 sec
    private static final int MAX_RETRY_TIMEOUT_MILLIS = 600000;   // 600 sec
    private static final int CACHE_POOL_TIMEOUT = 25;   // 25 minute, cause Kerberos login time out para(kdc_max_renewable_life default 30min)

    private LoadingCache<StoreURL, RestClient> POOL;
    private LoadingCache<StoreURL, RestHighLevelClient> HIGH_POOL;

    @PostConstruct
    public void init() {
        this.POOL = CacheFactory.create(new CacheLoader<StoreURL, RestClient>() {
            @Override
            public RestClient load(StoreURL storeURL) throws Exception {
                return createRestClient(storeURL);
            }
        }, new RemovalListener<StoreURL, RestClient>() {
            @Override
            public void onRemoval(RemovalNotification<StoreURL, RestClient> notification) {
                closeRestClient(notification.getKey(), notification.getValue());
            }
        }, CACHE_POOL_TIMEOUT);
        this.HIGH_POOL = CacheFactory.create(new CacheLoader<StoreURL, RestHighLevelClient>() {
            @Override
            public RestHighLevelClient load(StoreURL storeURL) throws Exception {
                return createHighRestClient(storeURL);
            }
        }, new RemovalListener<StoreURL, RestHighLevelClient>() {
            @Override
            public void onRemoval(RemovalNotification<StoreURL, RestHighLevelClient> notification) {
                closeHighRestClient(notification.getKey(), notification.getValue());
            }
        }, CACHE_POOL_TIMEOUT);
    }

    @PreDestroy
    public void cleanup() {
        POOL.asMap().forEach((storeURL, restClient) -> {
            closeRestClient(storeURL, restClient);
        });
        HIGH_POOL.asMap().forEach((storeURL, restClient) -> {
            closeHighRestClient(storeURL, restClient);
        });
    }

    /**
     * Get rest client.
     *
     * @param storeURL
     * @return
     */
    public RestClient getClient(StoreURL storeURL) {
        try {
            return POOL.get(storeURL);
        } catch (Exception e) {
            throw new EsServerException(e);
        }
    }

    /**
     * Get rest high level client.
     *
     * @param storeURL
     * @return
     */
    public RestHighLevelClient getRestHighLevelClient(StoreURL storeURL) {
        try {
            return HIGH_POOL.get(storeURL);
        } catch (Exception e) {
            throw new EsServerException(e);
        }
    }

    public String getMapping(StoreURL storeURL, String index) {
        try {
            RestClient restClient = getClient(storeURL);
            String source = JSON.toJSONString(Collections.emptyMap());
            HttpEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest(
                    "get", index + "/_mapping", Collections.emptyMap(), entity);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    public String getSetting(StoreURL storeURL, String index) {
        try {
            RestClient restClient = getClient(storeURL);
            String source = JSON.toJSONString(Collections.emptyMap());
            HttpEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);
            Response response =  restClient.performRequest(
                    "get", index + "/_settings", Collections.emptyMap(), entity);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private RestClient createRestClient(StoreURL storeURL) {
        try {
            return getRestClientBuilder(storeURL).build();
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    private RestHighLevelClient createHighRestClient(StoreURL storeURL) {
        try {
            return getRestHighLevelClientFI(storeURL);
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    private void closeRestClient(StoreURL storeURL, RestClient restClient) {
        try {
            restClient.close();
            LOG.info("Success to close expired rest client on url={0}", storeURL.getUrl());
        } catch (IOException e) {
            LOG.warn("close expired rest client got exception ", e);
        }
    }

    private void closeHighRestClient(StoreURL storeURL, RestHighLevelClient highLevelClient) {
        try {
            highLevelClient.close();
            LOG.info("Success to close expired rest high level client on url={0}", storeURL.getUrl());
        } catch (IOException e) {
            LOG.warn("close expired rest high level client got exception ", e);
        }
    }

    private RestHighLevelClient getRestHighLevelClientFI(StoreURL storeURL) throws Exception {
        return new RestHighLevelClient(getRestClientBuilder(storeURL));
    }

    private RestClientBuilder getRestClientBuilder(StoreURL storeURL) throws Exception {
        RestClientBuilder restClientBuilder;
        try {
            if (storeURL.isSecurityEnabled()) {
                System.setProperty("es.security.indication", "false");
                setSecConfig(storeURL);
            }
            Header[] defaultHeaders = new Header[]{new BasicHeader("Accept", "application/json"),
                    new BasicHeader("Content-type", "application/json")};
            List<HttpHost> hostList = buildHosts(storeURL);
            restClientBuilder = RestClient.builder(hostList.toArray(new HttpHost[]{}))
                    .setDefaultHeaders(defaultHeaders)
                    .setMaxRetryTimeoutMillis(MAX_RETRY_TIMEOUT_MILLIS)       // retry connect
                    .setRequestConfigCallback(
                            new RestClientBuilder.RequestConfigCallback() {
                                @Override
                                public RequestConfig.Builder customizeRequestConfig(
                                        RequestConfig.Builder requestConfigBuilder) {
                                    return requestConfigBuilder
                                            .setConnectTimeout(CONNECT_TIMEOUT) // socket tcp connect timeout
                                            .setSocketTimeout(SOCKET_TIMEOUT);  // socket tcp read socket data timeout
                                }
                            });
            LOG.info("success to create elasticsearch rest client url[{0}]", storeURL.getUrl());
            return restClientBuilder;
        } catch (Exception e) {
            LOG.error("failed to create elasticsearch rest client url[{0}].\n", e, storeURL.getUrl());
            throw e;
        }
    }

    /**
     * 192.168.1.50:9200,192.168.1.51:9200,192.168.1.52:9200
     *
     * @param storeURL
     * @return
     */
    private static List<HttpHost> buildHosts(StoreURL storeURL) {
        String esServerHost = storeURL.getUrl();
        List<HttpHost> hostList = new ArrayList<>();
        String[] hostArray = esServerHost.split(",");
        String protocol;
        if (storeURL.isSecurityEnabled()) {
            protocol = "https";
        } else {
            protocol = "http";
        }
        for (String host : hostArray) {
            String[] ipPort = host.split(":");
            HttpHost hostNew = new HttpHost(ipPort[0], Integer.valueOf(ipPort[1]), protocol);
            hostList.add(hostNew);
        }
        return hostList;
    }

    private static void setSecConfig(StoreURL storeURL) throws Exception {
        String krb5ConfFile = storeURL.getFilePath().get("krb5.conf");
        LOG.info("krb5ConfFile={0}", krb5ConfFile);
        System.setProperty("java.security.krb5.conf", krb5ConfFile);

        String jaasPath = storeURL.getFilePath().get("jaas.conf");
        modifyJaasConfigFile(jaasPath, storeURL);

        LOG.info("jaasPath={0}", jaasPath);
        System.setProperty("java.security.auth.login.config", jaasPath);
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

        System.setProperty("es.security.indication", "true");
        LOG.info("es.security.indication={0}", System.getProperty("es.security.indication"));
    }

    private static void modifyJaasConfigFile(String jaasPath, StoreURL storeURL) throws IOException {
        File jaasFile = new File(jaasPath);
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(jaasFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append('\n');
                }
                if (line.trim().startsWith("principal")) {
                    line = "principal=\"" + storeURL.getUserPrincipal() + "\"";
                }
                if (line.trim().startsWith("keyTab")) {
                    line = "keyTab=\"" + storeURL.getFilePath().get("user.keytab").replaceAll("\\\\", "/") + "\"";
                }
                stringBuilder.append(line.trim());
            }
        }
        try (FileWriter fileWriter = new FileWriter(jaasPath)) {
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
        }
    }
}