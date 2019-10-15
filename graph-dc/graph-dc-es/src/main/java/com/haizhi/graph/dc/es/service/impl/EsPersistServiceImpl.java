package com.haizhi.graph.dc.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.DataUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.util.ValidateUtils;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.es.service.EsPersistService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.core.EsBuilder;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.Source;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/11/14.
 */
@Service
public class EsPersistServiceImpl implements EsPersistService {

    private static final GLog LOG = LogFactory.getLogger(EsPersistServiceImpl.class);

    private static Cache<String, Boolean> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();

    @Autowired
    private DcMetadataCache dcMetadataCache;
    @Autowired
    private EsIndexDao esIndexDao;
    @Autowired
    private StoreUsageService storeUsageService;

    @Override
    public CudResponse bulkPersist(DcInboundDataSuo suo) {
        String graph = suo.getGraph();
        String type = suo.getSchema();
        CudResponse cudResponse = new CudResponse(graph, type);

        // check
        Domain domain = dcMetadataCache.getDomain(graph);
        if (!ValidateUtils.checkDomain(domain, suo, cudResponse)){
            LOG.warn(JSON.toJSONString(cudResponse));
            return cudResponse;
        }
        StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.ES);
        if (!ValidateUtils.checkStoreURL(storeURL, StoreType.ES, suo, cudResponse)){
            LOG.warn(JSON.toJSONString(cudResponse));
            return cudResponse;
        }

        // get index by store version
        String index = EsBuilder.getIndex(storeURL, graph, type);

        if (!createIndexIfNotExist(storeURL, index)) {
            cudResponse.setRowsErrors(suo.getRows().size());
            cudResponse.setMessage("cannot create index[" + index + "]");
            return cudResponse;
        }
        if (!createTypeIfNotExist(storeURL, index, type)) {
            cudResponse.setRowsErrors(suo.getRows().size());
            cudResponse.setMessage("cannot create type[" + index + "/" + type + "]");
            return cudResponse;
        }

        // persist
        GOperation operation = suo.getOperation();
        List<Source> sources = getSources(suo.getRows(), type, domain, operation);
        switch (operation) {
            case CREATE:
            case UPDATE:
            case CREATE_OR_UPDATE:
                cudResponse = esIndexDao.bulkUpsert(storeURL, index, type, sources);
                break;
            case DELETE:
                cudResponse = esIndexDao.delete(storeURL, index, type, sources);
                break;
            default:
                cudResponse.setRowsErrors(suo.getRows().size());
                cudResponse.setMessage("No operation specified in (CREATE,UPDATE,DELETE)");
                LOG.error(cudResponse.getMessage());
        }
        return cudResponse;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private List<Source> getSources(List<Map<String, Object>> rows, String type, Domain domain, GOperation op) {
        if (Objects.isNull(op)) {
            return Collections.emptyList();
        }
        List<Source> sources = new ArrayList<>();
        Map<String, SchemaField> fieldMap = domain.getSchema(type).getFieldMap();
        boolean deleteOperation = op == GOperation.DELETE;
        for (Map<String, Object> row : rows) {
            String objectKey = Getter.get(Keys.OBJECT_KEY, row);
            if (StringUtils.isBlank(objectKey)) {
                continue;
            }
            // elasticsearch._id
            Source source = new Source(objectKey);
            if (deleteOperation) {
                sources.add(source);
                continue;
            }
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String field = entry.getKey();
                String value = Objects.toString(entry.getValue(), "");
                // internalKeys
                if (Keys.internalKey(field) || Keys.OBJECT_KEY.equals(field)) {
                    source.addField(field, value);
                    continue;
                }
                // the fields configured to be stored in elasticsearch
                SchemaField sf = fieldMap.get(field);
                if (sf == null || !sf.isUseSearch()) {
                    continue;
                }
                FieldType fieldType = sf.getType();
                Object fieldValue = DataUtils.toObject(value, fieldType);
                // example: aaa\001bbb\001ccc
                if (fieldType == FieldType.STRING && fieldValue != null) {
                    String str = String.valueOf(fieldValue);
                    if (str.contains(Constants.SEPARATOR_001)) {
                        fieldValue = new ArrayList<>(Arrays.asList(str.split(Constants.SEPARATOR_001)));
                    }
                }
                source.addField(field, fieldValue);
            }
            sources.add(source);
        }
        return sources;
    }

    private boolean createIndexIfNotExist(StoreURL storeURL, String index) {
        if (containsCacheKey(index)) {
            return true;
        }
        if (esIndexDao.existsIndex(storeURL, index)){
            CACHE.put(index, true);
            return true;
        }
        if (esIndexDao.createIndex(storeURL, index)) {
            CACHE.put(index, true);
            return true;
        }
        return false;
    }

    private boolean createTypeIfNotExist(StoreURL storeURL, String index, String type) {
        String key = index + "/" + type;
        if (containsCacheKey(key)) {
            return true;
        }
        if (esIndexDao.existsType(storeURL, index, type)){
            CACHE.put(key, true);
            return true;
        }
        if (esIndexDao.createType(storeURL, index, type)) {
            CACHE.put(key, true);
            return true;
        }
        return false;
    }

    private boolean containsCacheKey(String key) {
        Boolean value = CACHE.getIfPresent(key);
        return value == null ? false : value;
    }
}
