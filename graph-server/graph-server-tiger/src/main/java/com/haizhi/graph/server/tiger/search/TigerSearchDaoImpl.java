package com.haizhi.graph.server.tiger.search;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.search.GQuery;
import com.haizhi.graph.server.api.gdb.search.GQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchBase;
import com.haizhi.graph.server.api.gdb.search.bean.GQueryType;
import com.haizhi.graph.server.tiger.constant.TigerKeys;
import com.haizhi.graph.server.tiger.repository.TigerRepo;
import com.haizhi.graph.server.tiger.util.TigerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2019/3/11.
 */
@Service
public class TigerSearchDaoImpl extends GdbSearchBase {

    private static final GLog LOG = LogFactory.getLogger(TigerSearchDaoImpl.class);

    @Autowired
    private TigerRepo tigerRepo;

    @Value("${server.tiger.graph.url:}")
    private String graphUrl;

    @Value("${server.tiger.query.url:}")
    private String queryUrl;

    @Value("${server.tiger.gsql.url:}")
    private String graphSqlUrl;


    /**
     * find data with query
     * @param gQuery: filter bean
     * @return : return two list about vertex and edges
     */
    @Override
    public GQueryResult search(GQuery gQuery) {
        GQueryResult gQueryResult = new GQueryResult();
        String gdbQuery = TigerWrapper.wrapGQuery(gQuery);
        LOG.info("gdbQuery: \n{0}", gdbQuery);
        String database = gQuery.getGraph();

        GQueryType gQueryType = gQuery.getType();
        String queryName;
        switch (gQueryType){
            case K_EXPAND:
                queryName = TigerKeys.K_EXPAND_QUERY_NAME;
                break;
            case SHORTEST_PATH:
                queryName = TigerKeys.SHORTEST_PATH_QUERY_NAME;
                break;
            case FULL_PATH:
                queryName = TigerKeys.FULL_PATH_QUERY_NAME;
                break;
            default:
                LOG.error("there is no GQueryType!");
                return gQueryResult;
        }

        // restService cant not urlEncoder
        try {
            Map<String, String> params = new HashMap<>();
            params.put("gdbQuery", gdbQuery);
            JSONObject tigerResponse = tigerRepo.executeQuery( queryUrl + "/" + database + "/" + queryName, params);
            LOG.info(JSONObject.toJSONString(tigerResponse, SerializerFeature.PrettyFormat));
            gQueryResult.setData(tigerResponse);
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
        return gQueryResult;
    }

    /**
     * find data with graph sql
     * @param graph: the tiger graph name
     * @param graphSql: the graph sql
     * @return : return single list, for example select * from company limit 10;
     */
    @Override
    public GQueryResult search(String graph, String graphSql) {
        GQueryResult gQueryResult = new GQueryResult();
        JSONObject tigerResponse = tigerRepo.execute(graphSqlUrl, graphSql);
        gQueryResult.setData(tigerResponse);
        return gQueryResult;
    }

}
