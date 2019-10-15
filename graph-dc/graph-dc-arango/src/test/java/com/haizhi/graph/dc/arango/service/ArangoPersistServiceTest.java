package com.haizhi.graph.dc.arango.service;

import com.arangodb.entity.DocumentImportEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/11/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class ArangoPersistServiceTest implements ArangoPersistService {

    private static final GLog LOG = LogFactory.getLogger(ArangoPersistServiceTest.class);
    private static Cache<String, Boolean> CACHE = CacheBuilder.newBuilder()
            .refreshAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, Boolean>() {
                @Override
                public Boolean load(String key) throws Exception {
                    return Boolean.TRUE;
                }
            });

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private GdbAdminDao gdbAdminDao;

    @Autowired
    private ArangoPersistService arangoPersistService;

    @Autowired
    private StoreUsageService storeUsageService;

    @Override
    @SuppressWarnings("all")
    public CudResponse bulkPersist(DcInboundDataSuo cuo) {
        String graph = cuo.getGraph();
        String tableName = cuo.getSchema();
        CudResponse cudResponse = new CudResponse(graph, tableName);

        Domain domain = dcMetadataCache.getDomain(graph);
        if (domain.invalid()) {
            cudResponse.setMessage("graph not found");
            LOG.warn("graph[{0}] not found", graph);
            return cudResponse;
        }
        String url = domain.getStoreUrl(StoreType.ES.name());
        StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.ES);
        if (createDbIfNotExist(storeURL, graph)) return cudResponse;

        // persist
        GOperation operation = cuo.getOperation();
        switch (operation) {
            case CREATE:
            case UPDATE:
            case CREATE_OR_UPDATE:
                cudResponse = update(cuo);
                break;
            case DELETE:
                cudResponse = delete(cuo);
                break;
            default:
                cudResponse.setMessage("No operation specified in (CREATE,UPDATE,DELETE)");
                LOG.error(cudResponse.getMessage());
        }
        return cudResponse;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private boolean createDbIfNotExist(StoreURL storeURL, String dbName){
        if (!containsCacheKey(dbName)){
            if (!gdbAdminDao.createDatabase(storeURL, dbName)) {
                return false;
            }
            CACHE.put(dbName, true);
        }

        return true;
    }


    private boolean containsCacheKey(String key){
        Boolean value = CACHE.getIfPresent(key);
        return value != null;
    }

    private CudResponse update(DcInboundDataSuo cuo){
        Object retObj = null;//arangoAdminDAO.bulkPersist(cuo);

        return BuildResponse(cuo,retObj);
    }

    private CudResponse delete(DcInboundDataSuo cuo){
        Object retObj = null;//arangoAdminDAO.delete(cuo);

        return BuildResponse(cuo,retObj);
    }


    private CudResponse BuildResponse(DcInboundDataSuo cuo, Object retObj){
        CudResponse cudResponse = new CudResponse();
        DocumentImportEntity ret = (DocumentImportEntity)retObj;
        cudResponse.setMessage(ret.getDetails());
        cudResponse.setRowsIgnored(ret.getIgnored());
        cudResponse.setGraph(cuo.getGraph());
        cudResponse.setSchema(cuo.getSchema());
        cudResponse.setRowsRead(cuo.getRows().size());

        return cudResponse;
    }

    @Test
    public void bulkPersist(){
        DcInboundDataSuo cuo = prepareData();
        arangoPersistService.bulkPersist(cuo);
    }

    private DcInboundDataSuo prepareData() {
        DcInboundDataSuo inboundDataCuo = new DcInboundDataSuo();

        inboundDataCuo.setOperation(GOperation.CREATE_OR_UPDATE);
        inboundDataCuo.setSchema("schema_from");
        inboundDataCuo.setGraph("graph_one");
        List<Map<String, Object>> data = Lists.newArrayListWithCapacity(5);
        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("object_key", "331");
        map1.put("name", "luban");
        map1.put("age", 20);

        Map<String, Object> map2 = Maps.newHashMap();
        map2.put("object_key", "321");
        map2.put("name", "houyi");
        map2.put("age", 29);

        Map<String, Object> map3 = Maps.newHashMap();
        map3.put("object_key", "311");
        map3.put("name", "yuji");
        map3.put("age", 13);

        Map<String, Object> map4 = Maps.newHashMap();
        map4.put("object_key", "301");
        map4.put("name", "sunshangxiang");
        map4.put("age", 18);

        data.add(map1);
        data.add(map2);
        data.add(map3);
        data.add(map4);
        inboundDataCuo.setRows(data);
        return inboundDataCuo;
    }


}
