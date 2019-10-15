package com.haizhi.graph.server.tiger.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.search.GQuery;
import com.haizhi.graph.server.api.gdb.search.bean.GDirection;
import com.haizhi.graph.server.api.gdb.search.bean.GQueryType;
import com.haizhi.graph.server.tiger.query.impl.TigerQBuilderImpl;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/3/14.
 */
public class TigerWrapperTest {

    private static final GLog LOG = LogFactory.getLogger(TigerWrapperTest.class);



    @Test
    public void buildExpression(){
        Map<String, Object> filter = buildFilterObject();
        StringBuilder expression = new StringBuilder();
        TigerQBuilderImpl tigerQBuilderImpl = new TigerQBuilderImpl();
        tigerQBuilderImpl.buildFilter(expression, filter);
        LOG.info("expression: \n{0}", expression);
    }

    public static GQuery buildGQuery(){
        GQuery gQuery = new GQuery();
        gQuery.setGraph("work_graph");
        gQuery.setType(GQueryType.FULL_PATH);
        gQuery.setDirection(GDirection.ANY);
        gQuery.setSize(100);

        Set<String> edgeTables = new HashSet<>();
        edgeTables.add("all_to_skill");
        edgeTables.add("company_person");

        Set<String> vertexTables = new HashSet<>();
        vertexTables.add("company");
        vertexTables.add("persons");
        vertexTables.add("skill");

        Set<GQuery.Vertex> startVertices = new HashSet<>();
        GQuery.Vertex startVertex1 = new GQuery.Vertex();
        startVertex1.setSchema("company");
        startVertex1.setObjectKey("c4");
        startVertices.add(startVertex1);

        GQuery.Vertex startVertex2 = new GQuery.Vertex();
        startVertex2.setSchema("company");
        startVertex2.setObjectKey("c1");
        startVertices.add(startVertex2);

        GQuery.Vertex startVertex3 = new GQuery.Vertex();
        startVertex3.setSchema("persons");
        startVertex3.setObjectKey("p3");
        startVertices.add(startVertex3);

        Set<GQuery.Vertex> endVertices = new HashSet<>();
        GQuery.Vertex endVertex1 = new GQuery.Vertex();
        endVertex1.setSchema("company");
        endVertex1.setObjectKey("222222222222222222");
        endVertices.add(endVertex1);

        GQuery.Vertex endVertex2 = new GQuery.Vertex();
        endVertex2.setSchema("persons");
        endVertex2.setObjectKey("p2");
        endVertices.add(endVertex2);

        gQuery.setVertexTables(vertexTables);
        gQuery.setEdgeTables(edgeTables);

        gQuery.setStartVertices(startVertices);
        gQuery.setEndVertices(endVertices);

        gQuery.setMaxDepth(10);
        gQuery.setMaxSize(100);
        gQuery.setOffset(44);

        gQuery.setDebug(true);

        Map<String, Object> filter = buildFilterObject();
        gQuery.setFilter(filter);

        return gQuery;
    }

    private static JSONObject buildFilterObject(){
        JSONObject filter = new JSONObject();
        JSONArray logicOperators = new JSONArray();
        logicOperators.add("OR");
        logicOperators.add("OR");
        filter.put("logicOperators", logicOperators);

        JSONArray rules = new JSONArray();
        filter.put("rules", rules);

        JSONObject rule1 = new JSONObject(true);
        JSONObject rule2 = new JSONObject(true);
        JSONObject rule3 = new JSONObject(true);
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);

        {
            rule1.put("schema", "company");
            rule1.put("schemaType", "vertex");
            rule1.put("field", "city");
            rule1.put("fieldType", "string");
            rule1.put("type", "term");
            {
                JSONArray term = new JSONArray();
                term.add("sayan");
                term.add("dongguan");
                rule1.put("values", term);
            }
        }

        {
            rule2.put("schema", "all_to_skill");
            rule2.put("schemaType", "edge");
            rule2.put("field", "quadrant");
            rule2.put("fieldType", "int");
            rule2.put("type", "range");
            {
                JSONArray ranges = new JSONArray();
                {
                    JSONObject range1 = new JSONObject(true);
                    range1.put("from", 1);
                    range1.put("to", 800);
                    range1.put("includeLower", false);
                    range1.put("includeUpper", true);
                    ranges.add(range1);
                }
                {
                    JSONObject range2 = new JSONObject(true);
                    range2.put("from", 1000);
                    range2.put("to", 3000);
                    range2.put("includeLower", true);
                    range2.put("includeUpper", true);
                    ranges.add(range2);
                }
                rule2.put("values", ranges);
            }
        }
        {
            JSONArray logicOperators3 = new JSONArray();
            logicOperators3.add("AND");
            logicOperators3.add("OR");
            rule3.put("logicOperators", logicOperators3);

            JSONArray rules3 = new JSONArray();
            rule3.put("rules", rules3);

            JSONObject rule31 = new JSONObject();
            JSONObject rule32 = new JSONObject();
            rules3.add(rule31);
            rules3.add(rule32);

            {
                rule31.put("schema", "persons");
                rule31.put("schemaType", "vertex");
                rule31.put("field", "quadrant");
                rule31.put("fieldType", "int");
                rule31.put("type", "term");
                {
                    JSONArray term = new JSONArray();
                    term.add(4);
                    term.add(5);
                    rule31.put("values", term);
                }
            }

            {
                rule32.put("schema", "persons");
                rule32.put("schemaType", "vertex");
                rule32.put("field", "quadrant");
                rule32.put("fieldType", "int");
                rule32.put("type", "term");
                {
                    JSONArray term = new JSONArray();
                    term.add(10);
                    term.add(20);
                    rule32.put("values", term);
                }
            }
        }
//        LOG.info(JSON.toJSONString(filter,SerializerFeature.PrettyFormat));
        return filter;
    }

}
