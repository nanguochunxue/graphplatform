package com.haizhi.graph.server.es6.index;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.ScriptSource;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.api.exception.EsServerException;
import com.haizhi.graph.server.es6.client.EsRestClient;
import com.haizhi.graph.server.es6.index.mapping.Template;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by tanghaiyang on 2019/5/9.
 */
@Repository
public class EsIndexDaoImpl implements EsIndexDao {

    private static final GLog LOG = LogFactory.getLogger(EsIndexDaoImpl.class);

    private static final String PUT = "PUT";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String HEAD = "HEAD";
    private static final String DELETE = "DELETE";

    @Autowired
    private EsRestClient esRestClient;

    @Override
    public void testConnect(StoreURL storeURL) {
        try {
            RestClient client = esRestClient.getClient(storeURL);
            Response response = client.performRequest("GET", "_cluster/health", Collections.emptyMap());
            LOG.info("Response status code: {0}", response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            LOG.error(e);
            throw new EsServerException(e);
        }
    }

    @Override
    public boolean existsIndex(StoreURL storeURL, String index) {
        RestClient client = esRestClient.getClient(storeURL);
        try {
            Response response = client.performRequest(HEAD, index);
            return response.getStatusLine().getReasonPhrase().equals("OK");
        } catch (IOException e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean existsType(StoreURL storeURL, String index, String type) {
        RestClient client = esRestClient.getClient(storeURL);
        try {
            Response response = client.performRequest(HEAD, new StringBuilder(index).append("/_mapping/").append
                    (type).toString());
            return response.getStatusLine().getReasonPhrase().equals("OK");
        } catch (IOException e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean createIndex(StoreURL storeURL, String index) {
        try {
            RestClient client = esRestClient.getClient(storeURL);
            IndexRequest request = new IndexRequest(index).source(Template.getSettings());
            request.opType(DocWriteRequest.OpType.CREATE);
            String source = request.source().utf8ToString();
            HttpEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);
            Response response = client.performRequest(PUT, index, Collections.emptyMap(), entity);
            if (RestStatus.OK.getStatus() != response.getStatusLine().getStatusCode()) {
                LOG.error("Failed to create index[{0}], response:\n{1}", index,
                        JSON.toJSONString(response, true));
                return false;
            }
            LOG.info("Success to create index[{0}]", index);
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean deleteIndex(StoreURL storeURL, String index) {
        try {
            RestClient client = esRestClient.getClient(storeURL);
            Response response = client.performRequest(DELETE, "/" + index + "?&pretty=true");
            if (RestStatus.OK.getStatus() != response.getStatusLine().getStatusCode()) {
                LOG.error("Failed to delete type[{0}], response:\n{2}", index,
                        JSON.toJSONString(response, true));
                return false;
            }
            LOG.info("Success to delete index[{0}]", index);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to delete index[{0}]", e, index);
        }
        return false;
    }

    @Override
    public boolean createType(StoreURL storeURL, String index, String type) {
        try {
            String url = index + "/_mapping/" + type;
            RestClient client = esRestClient.getClient(storeURL);
            IndexRequest request = new IndexRequest(index, type).source(Template.getTypeMapping());
            request.opType(DocWriteRequest.OpType.CREATE);
            String source = request.source().utf8ToString();
            LOG.audit("source :\n{0}", JSON.toJSONString(JSONObject.parseObject(source), true));
            HttpEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);
            Response response = client.performRequest(PUT, url, Collections.emptyMap(), entity);

            if (RestStatus.OK.getStatus() != response.getStatusLine().getStatusCode()) {
                LOG.error("Failed to create type[{0}/{1}], response:\n{2}", index, type,
                        JSON.toJSONString(response, true));
                return false;
            }
            LOG.info("Success to create type[{0}/{1}]", index, type);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to create type[{0}/{1}]", e, index, type);
        }
        return false;
    }

    @Override
    public CudResponse bulkUpsert(StoreURL storeURL, String index, String type, List<Source> sourceList) {
        CudResponse cudResponse = new CudResponse();
        if (sourceList == null || sourceList.isEmpty()) {
            LOG.error("It cannot upsert index with a EMPTY Source set:" + sourceList);
            cudResponse.setMessage("sourceList is null or empty");
            return cudResponse;
        }
        try {
            RestHighLevelClient highLevelClient = esRestClient.getRestHighLevelClient(storeURL);
            BulkRequest bulkRequest = new BulkRequest();
            for (Source source : sourceList) {
                bulkRequest.add(new IndexRequest(index, type, source.getId()).source(source.getSource()));
            }
            BulkResponse bulkResponse = highLevelClient.bulk(bulkRequest);
            if (bulkResponse.hasFailures()) {
                LOG.error("upsert failed {0}|{1}|{2}|{3} ", index, type, bulkResponse.buildFailureMessage(),
                        sourceList.size());
                int failedCount = 0;
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()){
                        failedCount++;
                    }
                }
                cudResponse.setRowsErrors(failedCount);
                return cudResponse;
            }
            cudResponse.setRowsAffected(sourceList.size());
            cudResponse.setSuccess(true);
            LOG.audit("Success to bulk upsert [{0}/{1}] records | size: [{2}]", index, type, sourceList.size());
        } catch (Exception e) {
            LOG.error("Failed to bulk upsert with type[{0}/{1}],data:\n{2}", e, index, type, JSON.toJSONString(sourceList));
        }
        return cudResponse;
    }

    @Override
    public CudResponse bulkScriptUpsert(StoreURL storeURL, String index, String type, List<ScriptSource> sources) {
        return new CudResponse();
    }

    @Override
    public CudResponse delete(StoreURL storeURL, String index, String type, List<Source> sources) {
        CudResponse cudResponse = new CudResponse();
        if (sources == null || sources.isEmpty()) {
            LOG.error("It cannot delete index with a EMPTY Source set:" + sources);
            cudResponse.setSuccess(false);
            return cudResponse;
        }
        try {
            RestHighLevelClient highLevelClient = esRestClient.getRestHighLevelClient(storeURL);
            BulkRequest bulkRequest = new BulkRequest();
            for (Source source : sources) {
                DeleteRequest dr = new DeleteRequest(index, type, source.getId());
                bulkRequest.add(dr);
            }
            BulkResponse bulkResponse = highLevelClient.bulk(bulkRequest);
            if (bulkResponse.hasFailures()) {
                LOG.error("One or more indexes delete failed with type[{0}/{1}] due to:\n{2}\n{3}",
                        index, type, bulkResponse.buildFailureMessage(), JSON.toJSONString(sources, true));
            } else {
                cudResponse.setSuccess(true);
                LOG.info("Success to bulk delete [{0}] records for type[{1}/{2}].", sources.size(), index, type);
            }
        } catch (Exception e) {
            cudResponse.setSuccess(false);
            LOG.error("Failed to bulk delete [{0}] records with type[{1}/{2}],data:\n{3}", e,
                    sources.size(), index, type, JSON.toJSONString(sources, true));
        }
        return cudResponse;
    }
}
