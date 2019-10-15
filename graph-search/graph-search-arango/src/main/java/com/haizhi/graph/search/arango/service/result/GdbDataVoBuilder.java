package com.haizhi.graph.search.arango.service.result;

import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.search.api.gdb.model.GdbDataVo;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.result.GKeys;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by tanghaiyang on 2019/4/16.
 */
public class GdbDataVoBuilder {

    /**
     * @vertexTablesRaw filter vertex table, because can not filter vertex in aql(with vertex is incomplete)
     * */
    public static GdbDataVo getTraverse(GdbQueryResult gdbQueryResult, Set<String> vertexTablesRaw, Set<String> retainVertices){
        List<Map<String, Object>> resultList = Getter.getListMap(gdbQueryResult.getData()) ;
        GdbDataVo gdbDataVo = new GdbDataVo();
        List<Map<String, Object>> vertexes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        Set<String> vertexKeySet = new HashSet<>();
        Set<String> edgeKeySet = new HashSet<>();
        if (CollectionUtils.isEmpty(resultList)){
            return gdbDataVo;
        }
        resultList.forEach(map -> {
            List<Map<String, Object>> subVertices = Getter.getListMap(map.get(GKeys.VERTICES));
            List<Map<String, Object>> subEdges = Getter.getListMap(map.get(GKeys.EDGES));
            addDocuments(subVertices,vertexKeySet,vertexes);
            addDocuments(subEdges,edgeKeySet,edges);
        });
        List<Map<String, Object>> vertexesFiltered = filterVertex(vertexes, vertexTablesRaw, retainVertices);  // filter vertex table
        gdbDataVo.setVertices(vertexesFiltered);
        gdbDataVo.setEdges(edges);
        return gdbDataVo;
    }

    /**
     * @vertexTablesRaw filter vertex table
     * */
    public static GdbDataVo getShortestPath(GdbQueryResult gdbQueryResult, Set<String> vertexTablesRaw, Set<String> retainVertices){
        List<Map<String, Object>> resultList = Getter.getListMap(gdbQueryResult.getData()) ;
        GdbDataVo gdbDataVo = new GdbDataVo();
        Set<Map<String, Object>> vertexesSet = new HashSet<>();
        Set<Map<String, Object>> edgesSet = new HashSet<>();
        if (CollectionUtils.isEmpty(resultList)){
            return gdbDataVo;
        }
        resultList.forEach(map -> {
            Map<String, Object> vertex = Getter.getMap(map.get(GKeys.NODE));
            Map<String, Object> edge = Getter.getMap(map.get(GKeys.EDGE));
            if (!CollectionUtils.isEmpty(vertex)) {
                vertexesSet.add(vertex);
            }
            if (!CollectionUtils.isEmpty(edge)) {
                edgesSet.add(edge);
            }
        });
        List<Map<String, Object>> vertexes = new ArrayList<>(vertexesSet);
        List<Map<String, Object>> edges = new ArrayList<>(edgesSet);
        List<Map<String, Object>> vertexesFiltered = filterVertex(vertexes, vertexTablesRaw, retainVertices);  // filter vertex table
        gdbDataVo.setVertices(vertexesFiltered);
        gdbDataVo.setVertices(vertexes);
        gdbDataVo.setEdges(edges);
        return gdbDataVo;
    }

    ///////////////////////////
    ///// private function
    //////////////////////////
    private static void addDocuments(List<Map<String, Object>> subList, Set<String> keySet, List<Map<String, Object>> documents){
        if (!CollectionUtils.isEmpty(subList)) {
            for(Map<String, Object> edge:subList){
                if(Objects.isNull(edge)) break;
                String key = edge.get(GKeys.KEY).toString();
                if(Objects.nonNull(key) && !keySet.contains(key)) {
                    keySet.add(key);
                    documents.add(edge);
                }
            }
        }
    }

    @SuppressWarnings("all")
    private static List<Map<String, Object>> filterVertex(List<Map<String, Object>> vertexes,
                         Set<String> vertexTablesRaw, Set<String> retainVertices){
        List<Map<String, Object>> result = new ArrayList<>();
        if(Objects.isNull(result)) return null;
        for(Map<String, Object> vertex:vertexes){
            String id = vertex.get(GKeys.ID).toString();
            if(retainVertices.contains(id)){
                result.add(vertex);
                continue;
            }
            if(Objects.nonNull(id) && !id.isEmpty()) {
                String[] tableIdArr = id.split("/");
                if(tableIdArr.length>1) {
                    String vertexTableName = tableIdArr[0];
                    if(vertexTablesRaw.contains(vertexTableName)){
                        result.add(vertex);
                    }
                }
            }
        }
        return result;
    }

}
