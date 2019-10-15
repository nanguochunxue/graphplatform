package com.haizhi.graph.server.arango.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.search.*;
import com.haizhi.graph.server.api.gdb.search.query.QBuilder;
import com.haizhi.graph.server.arango.client.ArangoClient;
import com.haizhi.graph.server.arango.constant.ServerArangoStatus;
import com.haizhi.graph.server.arango.search.builder.IdsBuilder;
import com.haizhi.graph.server.arango.search.parser.ArangoQueryParser;
import com.haizhi.graph.server.arango.search.parser.ArangoShortestPathParser;
import com.haizhi.graph.server.arango.search.parser.ArangoTraverseParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/1/18.
 */
@Service
public class ArangoSearchDaoImpl implements GdbSearchDao {

    private static final GLog LOG = LogFactory.getLogger(ArangoSearchDaoImpl.class);

    @Autowired
    private ArangoClient arangoClient;
    @Autowired
    private ArangoTraverseParser traverseParser;

    @Autowired
    private ArangoShortestPathParser shortestPathParser;

    private ArangoDB getClient(StoreURL storeURL) {
        return arangoClient.getClient(storeURL);
    }

    /**
     * some parameter validations of GdbQuery
     *
     * @param gdbQuery
     * @return
     * @author thomas
     */
    protected boolean preCheck(GdbQuery gdbQuery) {
        if (gdbQuery == null && StringUtils.isBlank(gdbQuery.getGraphSQL())) {
            LOG.error("Can't traverse with a empty GdbQuery or graph sql.");
            return false;
        }
        if ((gdbQuery.getOffset() != null && gdbQuery.getOffset() < 0) || (gdbQuery.getSize() != null && gdbQuery.getSize() < 0)) {
            LOG.error("illegal arguments. offset: {0}, size: {1}", gdbQuery.getOffset(), gdbQuery.getSize());
            return false;
        }
        return true;
    }

    /**
     * execute AQL to search arangodb and get the result
     *
     * @param db
     * @param sql
     * @param params
     * @return
     * @author thomas
     */
    protected List<Map<String, Object>> rawSearch(StoreURL storeURL, String db, String sql, Map<String, Object> params) {
        ArangoCursor<VPackSlice> cursor = null;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            ArangoDatabase arangoDB = getClient(storeURL).db(db);
            LOG.info("Arangodb search with sql: \n{0}", sql);
            cursor = arangoDB.query(sql, params, null, VPackSlice.class);
            cursor.forEachRemaining(vPackSlice -> {
                String json = vPackSlice.toString();
                try {
                    List<Map<String, Object>> res = JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
                    });
                    if (!CollectionUtils.isEmpty(res)) list.addAll(res);
                } catch (Exception e) {
                    try {
                        Map<String, Object> res = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
                        });
                        if (!CollectionUtils.isEmpty(res)) list.add(res);
                    } catch (Exception ignore) {
                        LOG.error("fail to parse json: \n{0}", json);
                    }
                }
            });
            LOG.info("Arangodb search done. Size: {0}", list.size());
        } catch (Exception e) {
            LOG.error("traverse error: ", e);
            throw new UnexpectedStatusException(ServerArangoStatus.ARANGODB_QUERRY_ERROR,e,e.getMessage());
        } finally {
            if (cursor != null)
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
        }
        return list;
    }

    /**
     * result pagination
     *
     * @param offset
     * @param size
     * @param originList
     * @return
     * @author thomas
     */
    protected <T> List<T> setUpPageResult(Integer offset, Integer size, List<T> originList) {
        if (offset == null || size == null) return originList;
        List<T> subList = originList;
        try {
            subList = originList.subList(offset, size + offset);
        } catch (Exception ignore) {
        }
        return subList;
    }

    /**
     * This method is expected to execute normal AQL (not traverse AQL). <br/>
     * But since the ArangoTraverseParser is used, the QBuilder in GdbQuery is parsed and traverse AQL (not normal AQL) is returned.<br/>
     * Therefore, use {@link #searchByGSQL(GdbQuery)} to execute normal AQL.
     *
     * @param gdbQuery use gdbQuery.getQuery()
     * @return
     * @author thomas
     */
    @Override
    public GdbQueryResult search(GdbQuery gdbQuery) {
        if (!preCheck(gdbQuery)) return new GdbQueryResult();

        try {
            QBuilder qBuilder = gdbQuery.getQuery();
            gdbQuery.setGraphSQL(traverseParser.toQuery(qBuilder.toXContent()));
            return this.searchByGSQL(gdbQuery);
        } catch (Exception e) {
            LOG.error("search error, while sql:\n{0}\n", e, gdbQuery.toString());
        }
        return new GdbQueryResult();
    }

    /**
     * execute specific AQL (normal AQL or traverse AQL)
     *
     * @param gdbQuery use gdbQuery.getGraphSQL()
     * @return
     * @author thomas
     */
    @Override
    public GdbQueryResult searchByGSQL(StoreURL storeURL, GdbQuery gdbQuery) {
        if (!preCheck(gdbQuery)) return new GdbQueryResult();
        String database = gdbQuery.getDatabase();
        String gql = gdbQuery.getGraphSQL();
        Map<String, Object> graphSQLParams = gdbQuery.getGraphSQLParams();
        GdbQueryResult gdbQueryResult = new GdbQueryResult();
        try {
            List<Map<String, Object>> list = rawSearch(storeURL, database, gql, graphSQLParams);
            LOG.info("Set up GdbQueryResult...");
            Integer offset = gdbQuery.getOffset();
            Integer size = gdbQuery.getSize();
            List<Map<String, Object>> subList = setUpPageResult(offset, size, list);

            // TODO: 18/2/8 projections
            gdbQueryResult.setData(subList);
            gdbQueryResult.setTotal(list.size());
            // TODO: 18/2/8 aggregation
            LOG.info("GdbQueryResult data size: {0}, total: {1}", subList.size(), list.size());
        }catch (Exception e){
            throw new UnexpectedStatusException(ServerArangoStatus.GET_ARANGO_RESULT_FAILE,e,e.getMessage());
        }
        return gdbQueryResult;
    }

    /**
     * generic method to parse the gdbQuery using the given parser, query, and then set up the result
     *
     * @param gdbQuery
     * @param parser
     * @return
     */
    protected GdbQueryResult parseAndQuery(StoreURL storeURL, GdbQuery gdbQuery, ArangoQueryParser parser) {
        GdbQueryResult gdbQueryResult = new GdbQueryResult();
        if (!preCheck(gdbQuery)) return gdbQueryResult;
        try {
            QBuilder qBuilder = gdbQuery.getQuery();
            List<String> sqls = parser.toMultiQuery(qBuilder.toXContent());
            LOG.info("toMultiQuery result size: {0}", sqls.size());
            if (!CollectionUtils.isEmpty(sqls)) {
                String database = gdbQuery.getDatabase();
                Integer timeout = gdbQuery.getTimeout();
                Map<String, Object> graphSQLParams = gdbQuery.getGraphSQLParams();
                LOG.info("prepare async multi-query...");
                List<Future<List<Map<String, Object>>>> futures = sqls.stream().map(gql -> {
                    return arangoClient.getAsyncExecutor().submit(() -> rawSearch(storeURL, database, gql, graphSQLParams));
                }).collect(Collectors.toList());
                List<Map<String, Object>> totalResult = new ArrayList<>();
                futures.forEach(future -> {
                    try {
                        List<Map<String, Object>> _list = future.get(timeout, TimeUnit.SECONDS);
                        if (!CollectionUtils.isEmpty(_list))
                            totalResult.addAll(_list);
                    } catch (Exception e) {
                        LOG.error("error when getting future, sqls: \n{0}\n, exception: \n{1}", JSON.toJSONString(sqls), e);
                        throw new UnexpectedStatusException(ServerArangoStatus.GET_ARANGO_RESULT_FAILE,e,e.getMessage());
                    }
                });
                LOG.info("Set up GdbQueryResult...");
                Integer offset = gdbQuery.getOffset();
                Integer size = gdbQuery.getSize();
                List<Map<String, Object>> subList = setUpPageResult(offset, size, totalResult);

                // TODO: 18/2/8 projections

                gdbQueryResult.setData(subList);
                gdbQueryResult.setTotal(totalResult.size());

                // TODO: 18/2/8 aggregation

                LOG.info("GdbQueryResult data size: {0}, total: {1}", subList.size(), totalResult.size());
            }
        } catch (Exception e) {
            LOG.error("query error with gdbQuery:\n{0}\n", e, gdbQuery.toString());
            throw new UnexpectedStatusException(ServerArangoStatus.QUERY_ARANGO_FAILE,e,e.getMessage());
        }
        return gdbQueryResult;
    }


    /**
     * parse {@link GdbQuery#getQuery()} to multi-traverse AQLs and execute them asynchronously
     *
     * @param gdbQuery use gdbQuery.getQuery()
     * @return
     * @author thomas
     */
    @Override
    public GdbQueryResult traverse(StoreURL storeURL, GdbQuery gdbQuery) {
        return parseAndQuery(storeURL, gdbQuery, traverseParser);
    }

    @Override
    public GdbQueryResult shortestPath(StoreURL storeURL, GdbQuery gdbQuery) {
        return parseAndQuery(storeURL, gdbQuery, shortestPathParser);
    }

    @Override
    public GdbQueryResult findByIds(StoreURL storeURL, GdbQuery gdbQuery) {
        GdbQueryResult gdbQueryResult = new GdbQueryResult();
        if (!preCheck(gdbQuery)) return gdbQueryResult;
        Map<String, String> aqlList = IdsBuilder.get(gdbQuery);

        Set<String> schemaNameList = aqlList.keySet();
        int total = 0;
//        Map<String, Object> totalResult = new HashMap<>();
        List<Map<String, Object>> totalResult = new ArrayList<>();
        if (!CollectionUtils.isEmpty(schemaNameList)) {
            String database = gdbQuery.getDatabase();
            Map<String, Object> graphSQLParams = gdbQuery.getGraphSQLParams();
            for (String schemaName : schemaNameList) {
                String aql = aqlList.get(schemaName);
                Future<List<Map<String, Object>>> future = arangoClient.getAsyncExecutor().submit(() ->
                        rawSearch(storeURL, database, aql, graphSQLParams));
                try {
                    List<Map<String, Object>> _list = future.get(10, TimeUnit.SECONDS);
                    if (!CollectionUtils.isEmpty(_list)) {
                        totalResult.addAll(_list);
                        total += _list.size();
                    }
                } catch (Exception e) {
                    LOG.error("error when getting future: ", e);
                    throw new UnexpectedStatusException(ServerArangoStatus.GET_ARANGO_RESULT_FAILE,e,e.getMessage());
                }
            }
            gdbQueryResult.setData(totalResult);
            gdbQueryResult.setTotal(total);
        }
        return gdbQueryResult;
    }

    @Override
    public GQueryResult search(GQuery gdbQuery) {
        return new GQueryResult();
    }

    @Override
    public GQueryResult search(String graph, String graphSql) {
        return new GQueryResult();
    }

    @Override
    public GdbQueryResult searchByGSQL(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult traverse(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult shortestPath(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult findByIds(GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

    @Override
    public GdbQueryResult search(StoreURL storeURL, GdbQuery gdbQuery) {
        return new GdbQueryResult();
    }

}
