package com.haizhi.graph.server.es.index;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.TimeRecorder;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.ScriptSource;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.api.exception.EsServerException;
import com.haizhi.graph.server.es.client.EsClient;
import com.haizhi.graph.server.es.client.EsRestClient;
import com.haizhi.graph.server.es.client.rest.http.HttpEntityBuilder;
import com.haizhi.graph.server.es.client.rest.http.RestResource;
import com.haizhi.graph.server.es.client.rest.http.RestResponses;
import com.haizhi.graph.server.es.index.mapping.Template;
import com.haizhi.graph.server.es.index.tcp.TcpResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhengyang on 2019/05/13
 */
@Repository
public class Es5IndexDaoImpl implements EsIndexDao {
    private static final GLog LOG = LogFactory.getLogger(Es5IndexDaoImpl.class);

    @Autowired
    private EsClient esClient;

    @Autowired
    private EsRestClient esRestClient;

    private static final int CONNECT_TIMEOUT = 15000;   // 15 sec

    private TransportClient getClient(StoreURL storeURL) {
        return esClient.getClient(storeURL);
    }

    private RestClient getRestClient(StoreURL storeURL) {
        return esRestClient.getClient(storeURL);
    }

    @Override
    public void testConnect(StoreURL storeURL) {
        try{
            TransportClient client = getClient(storeURL);
            ClusterHealthResponse response = client.admin().cluster().health(Requests.clusterHealthRequest())
                    .actionGet(TimeValue.timeValueMillis(CONNECT_TIMEOUT));
            int numberOfNodes = response.getNumberOfNodes();
            LOG.info("test connect successful, numberOfNodes: {0}", numberOfNodes);
        }catch (Exception e){
            LOG.error(e);
            throw new EsServerException(e);
        }
    }

    @Override
    public boolean existsIndex(StoreURL storeURL, String index) {
        IndicesExistsResponse response = this.getClient(storeURL).admin().indices()
                .exists(new IndicesExistsRequest(index)).actionGet();
        return response.isExists();
    }

    @Override
    public boolean existsType(StoreURL storeURL, String index, String type) {
        TypesExistsResponse response = this.getClient(storeURL).admin().indices()
                .typesExists(new TypesExistsRequest(new String[]{index}, type)).actionGet();
        return response.isExists();
    }

    @Override
    public boolean createIndex(StoreURL storeURL, String index) {
        boolean success = true;
        try {
            if (existsIndex(storeURL, index)) {
                LOG.info("The index[{0}] already exists", index);
                return true;
            }
            this.getClient(storeURL).admin().indices().prepareCreate(index)
                    .setSettings(Template.getSettings()).get();
            LOG.info("Success to create index[{0}]", index);
        } catch (Exception e) {
            success = false;
            LOG.error("Failed to create index[{0}]\n", e, index);
        }
        return success;
    }

    @Override
    public boolean deleteIndex(StoreURL storeURL, String index) {
        boolean success = true;
        try {
            if (!existsIndex(storeURL, index)) {
                LOG.warn("Failed to delete index[{0}], no such index", index);
                return true;
            }

            this.getClient(storeURL).admin().indices().prepareDelete(index).get();
            LOG.info("Success to delete index[{0}]", index);
        } catch (Exception e) {
            success = false;
            LOG.error("Failed to delete index[{0}]\n", e);
        }
        return success;
    }

    @Override
    public boolean createType(StoreURL storeURL, String index, String type) {
        boolean success = true;
        try {
            if (existsType(storeURL, index, type)) {
                LOG.info("The type[{0}/{1}] already exists", index, type);
                return true;
            }
            this.getClient(storeURL).admin().indices()
                    .preparePutMapping(index).setType(type)
                    .setSource(Template.getTypeMapping()).get();
            LOG.info("Success to create type[{0}/{1}]", index, type);
        } catch (Exception e) {
            success = false;
            LOG.error("Failed to create type[{0}/{1}]\n", e, 1, type);
        }
        return success;
    }

    @Override
    public CudResponse bulkUpsert(StoreURL storeURL, String index, String type, List<Source> sourceList) {
        CudResponse cudResponse = new CudResponse(index, type);
        cudResponse.setRowsRead(sourceList.size());
        if (sourceList.isEmpty()) {
            LOG.error("It cannot upsert index with a EMPTY Source set:" + sourceList);
            cudResponse.setMessage("sourceList is null or empty");
            return cudResponse;
        }
        try {
            TimeRecorder tr = new TimeRecorder().start();
            HttpEntity entity = HttpEntityBuilder.create(sourceList);
            Response resp = this.getRestClient(storeURL).performRequest(
                    RestResource.POST,
                    RestResource.bulk(index, type),
                    Collections.emptyMap(),
                    entity
            );
            // process response
            RestResponses.processBulkResponse(resp, cudResponse);
            cudResponse.setElapsedTime(tr.close().getElapsedTime());
        } catch (IOException e) {
            cudResponse.setMessage(e.getMessage());
            LOG.error("Failed to bulk upsert with type[{0}/{1}]", e, index, type);
        }
        return cudResponse;
    }

    @Override
    public CudResponse bulkScriptUpsert(StoreURL storeURL, String index, String type, List<ScriptSource> sources) {
        CudResponse cudResponse = new CudResponse(index, type, GOperation.CREATE_OR_UPDATE);
        if (sources == null || sources.isEmpty()) {
            LOG.error("Cannot script upsert index with a EMPTY ScriptSource set:" + sources);
            cudResponse.setSuccess(false);
            return cudResponse;
        }
        boolean success = true;
        try {
            TransportClient client = getClient(storeURL);
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (ScriptSource source : sources) {
                String upsertScript = source.getUpsertScript();
                if (StringUtils.isEmpty(upsertScript)) {
                    continue;
                }
                Script script = new Script(ScriptType.INLINE, "painless", upsertScript, source.getParams());
                UpdateRequestBuilder urb = client.prepareUpdate(index, type, source.getId())
                        .setScriptedUpsert(true).setScript(script).setRetryOnConflict(5);
                bulkRequest.add(urb);
            }
            if (bulkRequest.numberOfActions() < 1) {
                cudResponse.setSuccess(false);
                return cudResponse;
            }
            // response
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                LOG.error("One or more indexes script upsert failed with type[{0}/{1}] due to:\n{2}\n{3}",
                        index, type, bulkResponse.buildFailureMessage(), JSON.toJSONString(sources, true));
                cudResponse.setSuccess(false);
            } else {
                LOG.debug("Success to bulk script  upsert [{0}] records for type[{1}/{2}].", sources.size(), index,
                        type);
                int size = sources.size();
                cudResponse.setRowsRead(size);
                cudResponse.setRowsAffected(size);
                cudResponse.setSuccess(true);
            }
        } catch (Exception e) {
            success = false;
            LOG.error("Failed to bulk script upsert [{0}] records with type[{1}/{2}],data:\n{3}", e,
                    sources.size(), index, type, JSON.toJSONString(sources, true));
        }
        return cudResponse;
    }

    @Override
    public CudResponse delete(StoreURL storeURL, String index, String type, List<Source> sources) {
        CudResponse cudResponse = new CudResponse(index, type, GOperation.DELETE);
        if (sources == null || sources.isEmpty()) {
            LOG.error("It cannot delete index with a EMPTY Source set:" + sources);
            cudResponse.setSuccess(false);
            return cudResponse;
        }
        int size = sources.size();
        cudResponse.setRowsRead(size);
        try {
            TransportClient client = getClient(storeURL);
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (Source source : sources) {
                DeleteRequestBuilder dr = client.prepareDelete(index, type, source.getId());
                bulkRequest.add(dr);
            }
            // response
            BulkResponse bulkResponse = bulkRequest.get();
            TcpResponses.processBulkResponse(bulkResponse, cudResponse);
            if (!cudResponse.isSuccess()) {
                LOG.error("One or more indexes delete failed with type[{0}/{1}] due to:\n{2}",
                        index, type, cudResponse.getMessage());
            } else {
                LOG.info("Success to bulk delete [{0}] records for type[{1}/{2}].", size, index, type);
            }
        } catch (Exception e) {
            cudResponse.setFailure(e);
            LOG.error("Failed to bulk delete [{0}] records with type[{1}/{2}].", e, size, index, type);
        }
        return cudResponse;
    }
}
