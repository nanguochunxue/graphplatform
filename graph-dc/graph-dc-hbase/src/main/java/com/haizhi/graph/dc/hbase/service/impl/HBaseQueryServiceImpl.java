package com.haizhi.graph.dc.hbase.service.impl;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.KeyGetter;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.hbase.service.HBaseQueryService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQuery;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQueryResult;
import com.haizhi.graph.server.api.hbase.query.bean.TableResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/7/19.
 */
@Service
public class HBaseQueryServiceImpl implements HBaseQueryService {

    private static final GLog LOG = LogFactory.getLogger(HBaseQueryServiceImpl.class);

    private static KeyGetter keyGetter = KeyFactory.createKeyGetter();

    @Autowired
    HBaseQueryDao hBaseQueryDao;

    @Autowired
    HBaseQueryDao HBaseQueryDao;

    @Autowired
    private StoreUsageService storeUsageService;

    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo) {
        LOG.audit("KeySearchQo:\n{0}", JSON.toJSONString(searchQo));
        try {
            String database = searchQo.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(database, StoreType.Hbase);
            HBaseQuery hBaseQuery = buildHBaseQuery(searchQo);
            HBaseQueryResult hBaseQueryResult = HBaseQueryDao.searchByKeys(storeURL, hBaseQuery);
            KeySearchVo vo = new KeySearchVo();
            Map<String, TableResult> results = hBaseQueryResult.getResults();
            Map<String, Object> data = new HashMap<>();
            for (String schema : results.keySet()) {
                TableResult tableResult = results.get(schema);
                data.put(schema, tableResult.getRows());
            }
            vo.setData(data);
            return Response.success(vo);
        } catch (Exception e) {
            LOG.error("hbase searchByKeys error: \n{0}", searchQo);
        }
        return Response.error();
    }

    ////////////////////////////
    private HBaseQuery buildHBaseQuery(KeySearchQo searchQo) {
        HBaseQuery HBaseQuery = new HBaseQuery();
        HBaseQuery.setDatabase(searchQo.getGraph());
        for (Map.Entry<String, Set<String>> entry : searchQo.getSchemaKeys().entrySet()) {
            String table = entry.getKey();
            Set<String> objectKeys = entry.getValue();
            Set<String> rowKeys = new LinkedHashSet<>();
            for (String objectKey : objectKeys) {
                if (StringUtils.isBlank(objectKey)) {
                    continue;
                }
                rowKeys.add(keyGetter.getRowKey(objectKey));
            }
            HBaseQuery.addRowKeys(table, rowKeys);
        }
        return HBaseQuery;
    }


}
