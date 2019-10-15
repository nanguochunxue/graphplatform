package com.haizhi.graph.server.arango.search.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by thomas on 18/1/26.
 */
@Service
public class ArangoShortestPathParser extends ArangoQueryParser
{
    /**
     * parse to get multiple shortestPath AQLs.
     *
     * @param builder
     * @return
     */
    @Override
    public List<String> toMultiQuery(XContentBuilder builder)
    {
        JSONObject object = builder.rootObject();
        if(!preCheck(object)) return null;

        JSONObject graph = object.getJSONObject(GraphQBuilder.NAME);
        List<String> startVertices = graph.getJSONArray(GraphQBuilder.START_VERTICES_FIELD).stream().map(jsonObj -> (String)jsonObj).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(startVertices))
        {
            LOGGER.error("empty start vertices. return null");
            return null;
        }

        JSONArray endVerticesJsonArray = graph.getJSONArray(GraphQBuilder.END_VERTICES_FIELD);
        List<String> endVertices = null;
        if(!CollectionUtils.isEmpty(endVerticesJsonArray))
            endVertices = endVerticesJsonArray.stream().map(jsonObj -> (String)jsonObj).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        else
        {
            LOGGER.error("empty end vertices. return null");
            return null;
        }
        if(startVertices.size() != endVertices.size())
        {
            LOGGER.error("Size not equal. Start vertices size: {0}, end vertices size: {1}", startVertices.size(), endVertices.size());
            return null;
        }

        List<String> vertexTables = graph.getJSONArray(GraphQBuilder.VERTEX_TABLES_FIELD).stream().filter(Objects::nonNull).map(obj -> (String) obj).collect(Collectors.toList());
        List<String> edgeTables = graph.getJSONArray(GraphQBuilder.EDGE_TABLES_FIELD).stream().filter(Objects::nonNull).map(obj -> (String) obj).collect(Collectors.toList());
        String direction = graph.getString(GraphQBuilder.DIRECTION_FIELD);

        String shortestPathExpr = "WITH {{with}}\n" +
                "FOR v,e IN \n" +
                "{{direction}} SHORTEST_PATH '{{start}}' TO '{{end}}'\n" +
                "{{edges}}\n" +
                "RETURN {node:v, edge:e}";
        shortestPathExpr = shortestPathExpr.replace("{{with}}", StringUtils.join(vertexTables, ", "))
                .replace("{{direction}}", StringUtils.isEmpty(direction) ? DEFAULT_DIRECTION : direction)
                .replace("{{edges}}", StringUtils.join(edgeTables, ", "));

        final String queryExpr = shortestPathExpr;
        List<String> queryExprs = startVertices.stream().map(startVertex -> queryExpr.replace("{{start}}", startVertex)).collect(Collectors.toList());
        for(int i = 0; i < queryExprs.size(); ++i)
            queryExprs.set(i, queryExprs.get(i).replace("{{end}}", endVertices.get(i)));
        return queryExprs;
    }

    /**
     * do nothing. see {@link #toQuery(XContentBuilder)}
     *
     * @param object
     * @return
     */
    @Override
    protected String doQuery(JSONObject object)
    {
        return null;
    }

    /**
     * parse to get one single shortestPath AQL。<br/>
     *
     * @param builder
     * @return
     */
    @Override
    public String toQuery(XContentBuilder builder)
    {
        List<String> lists = toMultiQuery(builder);
        if(!CollectionUtils.isEmpty(lists))
        {
            if(lists.size() > 1)
                LOGGER.warn("multi shortestPath AQLs detected! return the first one. Please ensure this is what you want.");
            return lists.get(0);
        }
        return null;
    }

}

