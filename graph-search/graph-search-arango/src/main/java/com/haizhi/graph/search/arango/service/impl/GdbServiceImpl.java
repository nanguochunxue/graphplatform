package com.haizhi.graph.search.arango.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.BaseQo;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.model.qo.DcVertexEdgeQo;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.core.service.DcVertexEdgeService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.search.api.gdb.model.GdbDataVo;
import com.haizhi.graph.search.api.gdb.query.GdbAtlasParser;
import com.haizhi.graph.search.api.gdb.query.GdbParser;
import com.haizhi.graph.search.api.gdb.result.GdbResultBuilder;
import com.haizhi.graph.search.api.gdb.service.GdbService;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.search.api.model.qo.GdbSearchQo;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.vo.GdbAtlasVo;
import com.haizhi.graph.search.api.model.vo.GdbSearchVo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.arango.service.result.GdbDataVoBuilder;
import com.haizhi.graph.search.arango.service.result.GdbVoBuilder;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchDao;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.result.GResultBuilder;
import com.haizhi.graph.server.api.gdb.search.result.GResultType;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeBuilder;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import com.haizhi.graph.server.arango.constant.ServerArangoStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanghaiyang on 2018/6/5.
 */
@Service
public class GdbServiceImpl implements GdbService {

    private static final GLog LOG = LogFactory.getLogger(GdbServiceImpl.class);

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private GdbSearchDao gdbSearchDao;

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private DcVertexEdgeService dcVertexEdgeService;

    @Autowired
    private GdbAdminDao gdbAdminDao;

    private static Cache<String, Boolean> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();

    @Override
    public Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo) {
        LOG.audit("searchQo:\n{0}", JSON.toJSONString(searchQo, true));
        return doQuery(searchQo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo) {
        LOG.audit("searchQo:\n{0}", JSON.toJSONString(searchQo, true));
        GdbAtlasVo searchVo = new GdbAtlasVo();
        try {
            String graph = searchQo.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.GDB);
            Domain domain = dcMetadataCache.getDomain(graph);
            Set<String> vertexTables = domain.getRelatedVertexNames(searchQo.getEdgeTables());
            if(Objects.isNull(vertexTables)){
                LOG.error("get dc_vertex_edge failed for graph: {0}", graph);
                return Response.error("get vertexTables about edgeTables failed, check the error log!");
            }
            vertexTables.forEach(vertex -> {
                if (!gdbAdminDao.existsTable(storeURL, graph, vertex)) {
                    vertexTables.remove(vertex);
                }
            });
            GdbQuery gdbQuery = GdbAtlasParser.toGdbQuery(searchQo, vertexTables);
            GdbQueryResult gdbResult = gdbSearchDao.traverse(storeURL, gdbQuery);
            if (searchQo.isDebug()) {
                LOG.info("Gdb query result:\n{0}", JSON.toJSONString(gdbResult, true));
            }
            GResultType gResultType = GResultType.fromName(searchQo.getResultType());
            switch (gResultType) {
                case DEFAULT:
                    if (!searchQo.needAgg()) {
                        searchVo.setData(GResultBuilder.get(gdbResult.getData()));
                        break;
                    }
                    GTreeNode root = rebuildTree(searchQo, gdbResult);
                    searchVo.setData(GResultBuilder.getByTree(root));
                    break;
                case TREE:
                    searchVo.setTreeData(rebuildTree(searchQo, gdbResult));
                    break;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new UnexpectedStatusException(ServerArangoStatus.GDB_SEARCHATLAS_FAIL, e, e.getMessage());
        }
        return Response.success(searchVo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo) {
        if (searchQo.isDebug()) {
            LOG.info("searchQo:\n{0}", JSON.toJSONString(searchQo, true));
        }
        try {
            String graph = searchQo.getGraph();
            int timeout = searchQo.getInternalOption().getIntValue(BaseQo.TIMEOUT);
            boolean isDebug = searchQo.getInternalOption().getBooleanValue(BaseQo.DEBUG_FIELD);
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.GDB);
            GdbQuery gdbQuery = new GdbQuery(graph);
            Map<String, Set<String>> schemas = searchQo.getSchemaKeys();
            gdbQuery.setSchemas(schemas);
            if (timeout > 0) {
                gdbQuery.setTimeout(timeout);
            }
            gdbQuery.setDebugEnabled(isDebug);
            GdbQueryResult queryResult = gdbSearchDao.findByIds(storeURL, gdbQuery);
            KeySearchVo vo = new KeySearchVo();
            vo.setData(resultWrapper(queryResult));
            return Response.success(vo);
        } catch (Exception e) {
            LOG.error(e);
            throw new UnexpectedStatusException(ServerArangoStatus.GDB_SEARCHBYKEYS_FAIL, e, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<NativeSearchVo> searchNative(NativeSearchQo searchQo) {
        LOG.audit("searchQo:\n{0}", JSON.toJSONString(searchQo, true));
        try {
            String graph = searchQo.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.GDB);
            GdbQuery gdbQuery = new GdbQuery(graph);
            String query = Objects.toString(searchQo.getQuery(), "");
            if (!preCheck(query)) {
                throw new UnexpectedStatusException(ServerArangoStatus.GDB_SEARCHNATIVE_FORBIDDEN);
            }
            Map<String, Object> parameters = searchQo.getParameters();
            int timeout = searchQo.getInternalOption().getIntValue(BaseQo.TIMEOUT);
            boolean isDebug = searchQo.getInternalOption().getBooleanValue(BaseQo.DEBUG_FIELD);
            gdbQuery.setGraphSQL(query);
            gdbQuery.setGraphSQLParams(parameters);
            if (timeout > 0) {
                gdbQuery.setTimeout(timeout);
            }
            gdbQuery.setDebugEnabled(isDebug);
            GdbQueryResult gdbResult = gdbSearchDao.searchByGSQL(storeURL, gdbQuery);
            NativeSearchVo vo = NativeSearchVo.create(gdbResult.getData(), gdbResult.getAggData());
            return Response.success(vo);
        } catch (Exception e) {
            LOG.error(e);
            throw new UnexpectedStatusException(ServerArangoStatus.GDB_SEARCHNATIVE_FAIL, e, e.getMessage());
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    @SuppressWarnings("unchecked")
    private Response<GdbSearchVo> doQuery(GdbSearchQo searchQo) {
        try {
            String graph = searchQo.getGraph();
            Set<String> vertexTables = searchQo.getVertexTables();
            Set<String> startVertices = searchQo.getStartVertices();
            Set<String> endVertices = searchQo.getEndVertices();
            Set<String> retainVertices = new HashSet<>();
            if (!CollectionUtils.isEmpty(startVertices)) {
                retainVertices.addAll(startVertices);
            }
            if (!CollectionUtils.isEmpty(endVertices)) {
                retainVertices.addAll(endVertices);
            }
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.GDB);
            Set<String> vertexTablesExtra = buildExtraVertices(storeURL, searchQo);
            GdbQuery gdbQuery = GdbParser.toGdbQuery(searchQo, vertexTablesExtra);
            GdbQueryResult gdbQueryResult;
            GdbDataVo gdbDataVo;
            switch (searchQo.getType()) {
                case K_EXPAND:
                    gdbQueryResult = gdbSearchDao.traverse(storeURL, gdbQuery);
                    gdbDataVo = GdbDataVoBuilder.getTraverse(gdbQueryResult, vertexTables, retainVertices);
                    return GdbVoBuilder.get(gdbDataVo);
                case SHORTEST_PATH:
                    gdbDataVo = this.doShortestPath(storeURL, gdbQuery, vertexTables, retainVertices);
                    return GdbVoBuilder.get(gdbDataVo);
                case FULL_PATH:
                    gdbQueryResult = gdbSearchDao.traverse(storeURL, gdbQuery);
                    gdbDataVo = GdbDataVoBuilder.getTraverse(gdbQueryResult, vertexTables, retainVertices);
                    return GdbVoBuilder.get(gdbDataVo);
                default:
                    break;
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(ServerArangoStatus.GDB_SEARCHGDB_FAIL, e, e.getMessage());
        }
        return Response.success();
    }

    private GdbDataVo doShortestPath(StoreURL storeURL, GdbQuery gdbQuery, Set<String> vertexTablesRaw, Set<String> retainVertices) {
        GdbQueryResult gdbQueryResult = gdbSearchDao.shortestPath(storeURL, gdbQuery);
        GdbDataVo gdbDataVo = GdbDataVoBuilder.getShortestPath(gdbQueryResult, vertexTablesRaw, retainVertices);
        List<Map<String, Object>> edges = gdbDataVo.getEdges();
        if (Objects.nonNull(edges) && !edges.isEmpty()) {
            GraphQBuilder graphQBuilder = (GraphQBuilder) gdbQuery.getQuery();
            graphQBuilder.maxDepth(gdbDataVo.getEdges().size());
            gdbQueryResult = gdbSearchDao.traverse(storeURL, gdbQuery);
            gdbDataVo = GdbDataVoBuilder.getTraverse(gdbQueryResult, vertexTablesRaw, retainVertices);
        }
        return gdbDataVo;
    }

    private GTreeNode rebuildTree(GdbAtlasQo searchQo, GdbQueryResult gdbResult) {
        Set<String> startVertices = searchQo.getStartVertices();
        if (startVertices.size() != 1) {
            return null;
        }
        String rootVertex = startVertices.iterator().next();
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertex, gdbResult.getData());
        if (searchQo.isDebug()) {
            LOG.info("Gdb query result:\n{0}", JSON.toJSONString(root, true));
        }
        GdbResultBuilder.rebuild(searchQo, root);
        if (searchQo.isDebug()) {
            LOG.info("Gdb query result:\n{0}", JSON.toJSONString(root, true));
        }
        return root;
    }

    /**
     * add extra vertex tables
     */
    private Set<String> buildExtraVertices(StoreURL storeURL, GdbSearchQo searchQo) {
        String graph = searchQo.getGraph();
        DcVertexEdgeQo dcVertexEdgeQo = new DcVertexEdgeQo();
        dcVertexEdgeQo.setGraph(graph);
        dcVertexEdgeQo.setEdges(searchQo.getEdgeTables());
        Response<Set<String>> response = dcVertexEdgeService.findCollections(dcVertexEdgeQo);
        Set<String> extraVertexTables = response.getPayload().getData();
        if(Objects.isNull(extraVertexTables)) {
            LOG.audit("extraVertexTables is null, which come from dc_vertex_edge, dcVertexEdgeQo: {0}",
                    JSON.toJSONString(dcVertexEdgeQo,true));
            return searchQo.getVertexTables();
        }
        extraVertexTables.forEach(vertex -> {
            if (!this.existsTable(storeURL, graph, vertex)) {
                extraVertexTables.remove(vertex);
            }
        });
        LOG.audit("add extra vertex collections from dc_vertex_edge to resolve bug about AQL:with error, " +
                    "collections:\n{0}", JSON.toJSONString(extraVertexTables,true));
        return extraVertexTables;
    }

    /*
    * filter
    * */
    private boolean preCheck(String query) {
        String[] keywords = {"REMOVE", "UPDATE", "REPLACE", "INSERT", "UPSERT"};  // forbidden Arango DDL
        for (String keyword : keywords) {
            if (StringUtils.containsIgnoreCase(query, keyword)) {
                LOG.error("find native search gql contain forbidden keyword:{0}", keyword);
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resultWrapper(GdbQueryResult queryResult) {
        List<Map<String, Object>> mapList = Getter.getListMap(queryResult.getData());
        Map<String, Object> result = new HashMap<>();
        if(Objects.isNull(mapList)){
            return result;
        }
        mapList.forEach(map -> {
            try {
                if (!map.containsKey(Keys._ID)) {
                    LOG.error("result has no attribute _id: {0}", JSON.toJSONString(map, true));
                    return;
                }
                String _id = map.get(Keys._ID).toString();
                String[] schemaArr = _id.split("/");
                String schema = schemaArr[0];
                Object oldObject = result.get(schema);
                List<Map<String, Object>> oldList;
                if (Objects.nonNull(oldObject) && oldObject instanceof List) {
                    oldList = (List<Map<String, Object>>) oldObject;
                } else {
                    oldList = Lists.newArrayList();
                }
                oldList.add(map);
                result.put(schema, oldList);
            } catch (Exception e) {
                LOG.error(e);
            }
        });
        return result;
    }

    private boolean existsTable(StoreURL storeURL, String graph, String vertex) {
        String key = graph + "." + vertex;
        Boolean value = CACHE.getIfPresent(key);
        boolean isExist = value == null ? false : value;
        if (isExist) {
            return true;
        }
        if (gdbAdminDao.existsTable(storeURL, graph, vertex)) {
            CACHE.put(key, true);
            return true;
        }
        return false;
    }

}
