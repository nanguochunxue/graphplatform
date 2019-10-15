package com.haizhi.graph.server.arango.search.parser;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.bean.Sort;
import com.haizhi.graph.server.api.gdb.search.parser.QueryParser;
import com.haizhi.graph.server.api.gdb.search.query.*;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 18/2/7.
 */
public class ArangoTraverseParserTest
{
    private final QueryParser parser = new ArangoTraverseParser();
    private final String HOST = "192.168.1.54";
    private final int PORT = 8531;
    private final String USER = "root";
    private final String PASSWORD = "haizhi";
    private final String DB = "csytest1";

    private GdbQuery gdbQuery;

    @Before
    public void createGdbQuery(){
        gdbQuery = new GdbQuery(DB);

        /* graph builder */
        GraphQBuilder graphBuilder = QBuilders.graphQBuilder(
                Arrays.asList("Person", "Company"),
                Arrays.asList("invest", "officer", "tradable_share"));
        graphBuilder.startVertices(Arrays.asList("Company/67B82820FBDDF282EA3A56255D20A4EA", "Company/70F7EA0C706AC9900AF2B391204D05E1"));
        graphBuilder.endVertices(Arrays.asList("Person/300D26F4109573C7C90EEAA143528E01", "Person/95343E22E3AF11F0E30B98F728734CB4", "Person/3143B2BAE55A9009C257DD60622D51CA"));

        //set graph option
        graphBuilder.direction("ANY");
        graphBuilder.maxDepth(1);
        graphBuilder.offset(0);
        graphBuilder.size(500);
        graphBuilder.sort(new Sort().add("v._id").add(new Sort.Order("e._id", Sort.Direction.DESC)));

        gdbQuery.setQuery(graphBuilder);
        //no filter
        graphBuilder.setQuery(null);
    }

    @Test
    public void queryWithoutFilter(){
        XContentBuilder xContent = gdbQuery.getQuery().toXContent();
        // graphSQL
        List<String> graphSQLs = parser.toMultiQuery(xContent);

        ArangoDB arangoDB = new ArangoDB.Builder().host(HOST, PORT).user(USER).password(PASSWORD).maxConnections(10).timeout(1000).build();
        List<Map<String, List<Map<String, Object>>>> paths = getGraphPaths(graphSQLs, arangoDB);
        Assert.assertFalse(CollectionUtils.isEmpty(paths));
        System.out.println(paths.size());
    }

    @Test
    public void queryWithFilter(){
        GraphQBuilder graphQBuilder = (GraphQBuilder) gdbQuery.getQuery();

        //set up filter
        BoolQBuilder boolQBuilder = new BoolQBuilder();
        boolQBuilder.should(new VertexQBuilder("Person","v", new TermQBuilder("name", "'王世豪'")))
                .should(new VertexQBuilder("Company","v", new TermQBuilder("province", "'河南'")));
        graphQBuilder.setQuery(boolQBuilder);

        XContentBuilder xContent = gdbQuery.getQuery().toXContent();
        // graphSQL
        List<String> graphSQLs = parser.toMultiQuery(xContent);

        ArangoDB arangoDB = new ArangoDB.Builder().host(HOST, PORT).user(USER).password(PASSWORD).maxConnections(10).timeout(1000).build();
        List<Map<String, List<Map<String, Object>>>> paths = getGraphPaths(graphSQLs, arangoDB);
        Assert.assertFalse(CollectionUtils.isEmpty(paths));
        System.out.println(paths.size());
    }

    /**
     * process arangodb cursor and get the traverse path results
     *
     * @param graphSQLs
     * @param arangoDB
     * @return
     */
    private List<Map<String, List<Map<String, Object>>>> getGraphPaths(List<String> graphSQLs, ArangoDB arangoDB)
    {
        List<Map<String, List<Map<String, Object>>>> paths = new ArrayList<>();
        graphSQLs.forEach(graphSQL -> {
            System.out.println("================graphSQL===================");
            System.out.println(graphSQL);
            ArangoCursor<VPackSlice> cursor = arangoDB.db(DB).query(graphSQL, null, null, VPackSlice.class);
            ObjectMapper objectMapper = new ObjectMapper();
            cursor.forEachRemaining(vPackSlice -> {
                try {
                    Map<String, List<Map<String, Object>>> map = objectMapper.readValue(vPackSlice.toString(), new TypeReference<Map<String, List<Map<String, Object>>>>(){});
                    paths.add(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try {
                cursor.close();
                arangoDB.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return paths;
    }
}
