package com.haizhi.graph.server.api.gdb.search.result.path;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.haizhi.graph.common.constant.Fields;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.gdb.search.result.GEdge;

import java.util.*;

/**
 * Created by chengmo on 2018/3/8.
 */
public class GResult {

    //private Set<String> startVerteice = new LinkedHashSet<>();
    //private Map<String, GPath> pathMap = new LinkedHashMap<>();
    private List<GPath> pathList = new ArrayList<>();
    private List<Map<String, Object>> data;

    public GResult(List<Map<String, Object>> data) {
        this.data = data;
        this.initialize();
    }

    public boolean existsPath(String startVertex, String endVertex){
        for (GPath path : pathList) {
            String str = path.firstVertex() + path.lastVertex();
            if (str.equals(startVertex + endVertex) || str.equals(endVertex + startVertex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取相关的数据
     *
     * @param startVertex
     * @param endVertex
     * @return
     */
    public List<Map<String, Object>> getData(String startVertex, String endVertex) {
        List<GPath> existPathList = new ArrayList<>();
        for (GPath path : pathList) {
            String str = path.firstVertex() + path.lastVertex();
            if (str.equals(startVertex + endVertex) || str.equals(endVertex + startVertex)) {
                existPathList.add(path);
            }
        }
        Set<Integer> indexSet = new HashSet<>();
        for (GPath path : existPathList) {
            for (GEdge pair : path.pairs()) {
                indexSet.add(pair.index());
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (indexSet.contains(i)) {
                resultList.add(data.get(i));
            }
        }
        return resultList;
    }

    public List<String> pathIds() {
        List<String> results = new ArrayList<>();
        for (GPath path : pathList) {
            results.add(path.id());
        }
        return results;
    }

    public String string() {
        return JSON.toJSONString(pathIds(), true);
    }

    private void initialize() {
        // 构造全路径列表
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> source = data.get(i);
            JSONArray jsonArray = new JSONArray().fluentAddAll(Getter.getListMap(source.get("vertices")));
            String fromVertex = jsonArray.getJSONObject(0).getString(Fields.ID);
            String toVertex = jsonArray.getJSONObject(1).getString(Fields.ID);
            GEdge pair = new GEdge(fromVertex, toVertex, i);
            System.out.println(pair.getId());
            addGPair(pair);
        }
    }

    /**
     * A1=B1
     * A2=X2
     * X2=B2
     * X2=B3
     * A1=X2
     * <p>
     * A1->B1
     * A2->X2->B2
     * A2->X2->B3
     * A1->X2->B2
     * A1->X2->B3
     *
     * @param curPair
     */
    private void addGPair(GEdge curPair) {
        String curFromVertex = curPair.fromVertex();
        String curToVertex = curPair.toVertex();


        // 第一条数据直接添加
        if (pathList.isEmpty()) {
            addGPath(curPair);
            return;
        }

        // 是否拼接到已有路径的结尾
        for (GPath path : pathList) {
            if (path.lastVertex().equals(curFromVertex)) {
                path.joinPair(curPair);
                return;
            }
        }

        Map<String, List<GEdge>> newPathMap = new LinkedHashMap<>();
        for (int i = 0; i < pathList.size(); i++) {
            List<GEdge> pairs = pathList.get(i).pairs();
            int beforeIndex = -1;   // 匹配需要拼接的已有路径前面部分的下标
            int afterIndex = -1;    // 匹配需要拼接的已有路径后面部分的下标
            for (int j = 0; j < pairs.size(); j++) {
                GEdge pair = pairs.get(j);
                String fromVertex = pair.fromVertex();
                if (fromVertex.equals(curFromVertex)) {
                    beforeIndex = j;
                }
                if (fromVertex.equals(curToVertex)) {
                    afterIndex = j;
                }
            }

            if (beforeIndex > 0) {
                List<GEdge> subPairs = pairs.subList(0, beforeIndex);
                List<GEdge> newPairs = new ArrayList<>();
                for (GEdge subPair : subPairs) {
                    newPairs.add(subPair.clone());
                }
                // 如果是匹配了前面部分，需要把当前的数据放在末尾
                newPairs.add(curPair);
                newPathMap.put(GPath.id(newPairs), newPairs);
            }
            if (afterIndex >= 0) {
                List<GEdge> subPairs = pairs.subList(afterIndex, pairs.size());
                List<GEdge> newPairs = new ArrayList<>();
                // 如果是匹配了后面部分，需要把当前的数据放在开头
                newPairs.add(curPair);
                for (GEdge subPair : subPairs) {
                    newPairs.add(subPair.clone());
                }
                newPathMap.put(GPath.id(newPairs), newPairs);
            }
        }

        // 如果没有匹配到已有路径，新建一个路径
        if (newPathMap.isEmpty()) {
            addGPath(curPair);
        }

        for (List<GEdge> pairs : newPathMap.values()) {
            GPath newPath = new GPath().joinPairs(pairs);
            pathList.add(newPath);
        }
    }

    private void addGPath(GEdge pair) {
        pathList.add(new GPath().joinPair(pair));
    }
}
