package com.haizhi.graph.server.arango.search;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.bean.CollectionType;
import com.haizhi.graph.server.api.gdb.search.bean.Sort;
import com.haizhi.graph.server.api.gdb.search.query.*;
import com.haizhi.graph.server.api.gdb.search.result.Projections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by thomas on 18/2/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArangoSearchDAOImplTest
{
    @Autowired
    private ArangoSearchDaoImpl arangoSearchDAO;

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
    public void traverseWithoutFilter(){
        GdbQueryResult gdbQueryResult = arangoSearchDAO.traverse(gdbQuery);
        Assert.assertNotNull(gdbQueryResult);
        System.out.println(JSON.toJSONString(gdbQueryResult, true));
    }

    @Test
    public void traverseWithFilter(){
        GraphQBuilder graphQBuilder = (GraphQBuilder) gdbQuery.getQuery();

        //set up filter
        BoolQBuilder boolQBuilder = new BoolQBuilder();
        boolQBuilder.should(new VertexQBuilder("Person","v", new TermQBuilder("name", "'王世豪'")))
                .should(new VertexQBuilder("Company","v", new TermQBuilder("province", "'河南'")));
        graphQBuilder.setQuery(boolQBuilder);

        //set projections
        Projections projections = new Projections();
        projections.addProjection(CollectionType.EDGE, "invest", "fieldNotUsed");
        projections.addProjection(CollectionType.EDGE, "officer", "fieldNotUsed");
        graphQBuilder.projections(projections);

        GdbQueryResult gdbQueryResult = arangoSearchDAO.traverse(gdbQuery);
        Assert.assertNotNull(gdbQueryResult);
        System.out.println(gdbQueryResult);
    }

    @Test
    public void shortestPath()
    {
        gdbQuery = new GdbQuery(DB);

        /* graph builder */
        GraphQBuilder graphBuilder = QBuilders.graphQBuilder(
                Arrays.asList("Person", "Company"),
                Arrays.asList("invest", "officer", "tradable_share"));
        graphBuilder.startVertices(Collections.singletonList("Company/70F7EA0C706AC9900AF2B391204D05E1"));
        graphBuilder.endVertices(Collections.singletonList("Person/95343E22E3AF11F0E30B98F728734CB4"));

        //set graph option
        graphBuilder.direction("ANY");
        gdbQuery.setQuery(graphBuilder);
        //no filter
        graphBuilder.setQuery(null);

        GdbQueryResult gdbQueryResult = arangoSearchDAO.shortestPath(gdbQuery);
        Assert.assertNotNull(gdbQueryResult);
        System.out.println(gdbQueryResult);
    }
}
