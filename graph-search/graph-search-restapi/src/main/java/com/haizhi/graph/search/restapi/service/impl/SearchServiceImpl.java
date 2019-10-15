package com.haizhi.graph.search.restapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.concurrent.executor.AsyncJoinExecutor;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.search.api.constant.EsVersion;
import com.haizhi.graph.search.api.constant.GdbVersion;
import com.haizhi.graph.search.api.constant.HBaseVersion;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;
import com.haizhi.graph.search.api.service.SearchService;
import com.haizhi.graph.search.restapi.manager.EsManager;
import com.haizhi.graph.search.restapi.manager.GdbManager;
import com.haizhi.graph.search.restapi.manager.HBaseManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chengmo on 2019/4/23.
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final GLog LOG = LogFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private GdbManager gdbManager;
    @Autowired
    private EsManager esManager;
    @Autowired
    private HBaseManager hBaseManager;
    @Autowired
    private DcMetadataCache dcMetadataCache;

    private static final String storeTypePara = "storeType";

    @Override
    public Response<SearchVo> search(SearchQo searchQo) {
        LOG.audit("SearchQo:\n{0}", JSON.toJSONString(searchQo));
        return esManager.search(searchQo, getEsVersion(searchQo.getGraph()));
    }

    @Override
    public Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo) {
        LOG.audit("GdbSearchQo:\n{0}", JSON.toJSONString(searchQo));
        return gdbManager.searchGdb(searchQo, getGdbVersion(searchQo.getGraph()));
    }

    @Override
    public Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo) {
        LOG.audit("GdbAtlasQo:\n{0}", JSON.toJSONString(searchQo));
        return gdbManager.searchAtlas(searchQo, getGdbVersion(searchQo.getGraph()));
    }

    @Override
    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo) {
        LOG.audit("KeySearchQo:\n{0}", JSON.toJSONString(searchQo));
        JSONObject option = searchQo.getInternalOption();
        Domain domain = dcMetadataCache.getDomain(searchQo.getGraph());
        Map<String, Schema> schemaMap = domain.getSchemaMap();
        Map<String, StoreType> storeTypeMap = new HashMap<>();  // schemaName, StoreType
        for (String schemaName : searchQo.getSchemaKeys().keySet()) {
            Schema sch = schemaMap.get(schemaName);
            if (Objects.isNull(sch)) {
                LOG.error("schemaMap has no schemaName: {0}", schemaName);
                continue;
            }
            if (sch.isUseSearch()) {
                storeTypeMap.put(schemaName, StoreType.ES);
                continue;
            }
            if (sch.isUseHBase()) {
                storeTypeMap.put(schemaName, StoreType.Hbase);
                continue;
            }
            if (sch.isUseGraphDb()) {
                storeTypeMap.put(schemaName, StoreType.GDB);
            }
        }
        Set<StoreType> storeTypeSet = new HashSet<>();    // check how many StoreType in searchQo
        storeTypeMap.entrySet().forEach(entry -> storeTypeSet.add(entry.getValue()));
        if (storeTypeSet.size() > 1) {                    // there is more than one StoreType
            return searchByKeysAsync(storeTypeMap, searchQo);
        }
        StoreType storeType = null;
        if (option.containsKey(storeTypePara)) {
            storeType = StoreType.fromCode(option.getString(storeTypePara));
        } else if (storeTypeSet.size() == 1) {
            storeType = storeTypeSet.iterator().next();
        }
        if (Objects.isNull(storeType)) {
            return Response.error("there is no valid storeType from Domain cache, check the mysql dc_schema or dc_store!");
        }
        return searchByKeysSync(storeType, searchQo);
    }

    @Override
    public Response<NativeSearchVo> searchNative(NativeSearchQo searchQo) {
        LOG.audit("NativeSearchQo:\n{0}", JSON.toJSONString(searchQo));
        JSONObject option = searchQo.getInternalOption();
        if (option.containsKey(storeTypePara)) {
            StoreType type = StoreType.fromCode(option.getString(storeTypePara));
            switch (type) {
                case ES:
                    return esManager.searchNative(searchQo, getEsVersion(searchQo.getGraph()));
                case GDB:
                    return gdbManager.searchNative(searchQo, getGdbVersion(searchQo.getGraph()));
                case Hbase:
                    break;
            }
        }
        return gdbManager.searchNative(searchQo, getGdbVersion(searchQo.getGraph()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response<Object> executeProxy(String request) {
        LOG.audit("ProxyQo:\n{0}", request);
        String graph = getGraph(request);
        if (Objects.isNull(graph)) {
            return Response.error("request has no graph info");
        }
        return esManager.executeProxy(request, getEsVersion(graph));
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private GdbVersion getGdbVersion(String graph) {
        return GdbVersion.ATLAS;
    }

    private EsVersion getEsVersion(String graph) {
        return EsVersion.V5_4_3;
    }

    private HBaseVersion getHBaseVersion(String graph) {
        return HBaseVersion.V1_3_1;
    }

    private static String getGraph(String request) {
        if (StringUtils.isEmpty(request)) {
            LOG.error("para parse fail, check para[request]!");
            return null;
        }
        String[] body = request.split("\n");
        String[] headLine;
        String[] firstLine = body[0].trim().split(" ");
        if (firstLine.length == 1) {
            return firstLine[0];
        } else if (firstLine.length == 2) {
            headLine = firstLine;
        } else {
            LOG.error("para fail, check first line!");
            return null;
        }
        if (headLine.length != 2) {
            LOG.error("para head fail, check second line!");
            return null;
        }
        String uri = headLine[1].trim();
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        String[] uriPathArr = uri.split("/");
        String firstPathPara = uriPathArr[1];
        if (!firstPathPara.startsWith("_")) {
            return firstPathPara;
        }
        return null;
    }

    private Response<KeySearchVo> searchByKeysAsync(Map<String, StoreType> storeTypeMap, KeySearchQo searchQo) {
        Map<StoreType, KeySearchQo> searchQoMap = new HashMap<>();
        for (String schema : storeTypeMap.keySet()) {
            StoreType storeType = storeTypeMap.get(schema);
            KeySearchQo qo = new KeySearchQo();
            qo.setGraph(searchQo.getGraph());
            Map<String, Set<String>> schemaKeys = new HashMap<>();
            schemaKeys.put(schema, searchQo.getSchemaKeys().get(schema));
            qo.setSchemaKeys(schemaKeys);
            searchQoMap.put(storeType, qo);
        }
        AsyncJoinExecutor<Response<KeySearchVo>> joinExecutor = new AsyncJoinExecutor<>();
        for (Map.Entry<StoreType, KeySearchQo> entry : searchQoMap.entrySet()) {
            joinExecutor.join(() -> searchByKeysSync(entry.getKey(), entry.getValue()));
        }
        Map<String, Response<KeySearchVo>> results = joinExecutor.actionGet();
        return KeySearchVoAgg(results);
    }

    @SuppressWarnings("unchecked")
    private Response<KeySearchVo> searchByKeysSync(StoreType storeType, KeySearchQo searchQo) {
        switch (storeType) {
            case ES:
                return esManager.searchByKeys(searchQo, getEsVersion(searchQo.getGraph()));
            case GDB:
                return gdbManager.searchByKeys(searchQo, getGdbVersion(searchQo.getGraph()));
            case Hbase:
                return hBaseManager.searchByKeys(searchQo, getHBaseVersion(searchQo.getGraph()));
        }
        return Response.error("searchByKeysSync no valid storeType!");
    }

    @SuppressWarnings("unchecked")
    private Response<KeySearchVo> KeySearchVoAgg(Map<String, Response<KeySearchVo>> keySearchVos) {
        Map<String, Object> data = new HashMap<>();  // <schema, Map<String, Object>>
        keySearchVos.entrySet().forEach(entry -> {
            Response<KeySearchVo> response = entry.getValue();
            data.putAll(response.getPayload().getData().getData());
        });
        KeySearchVo finalKeySearchVo = KeySearchVo.create(data);
        return Response.success(finalKeySearchVo);
    }

}
