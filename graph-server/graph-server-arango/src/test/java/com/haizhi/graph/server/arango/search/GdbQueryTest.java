package com.haizhi.graph.server.arango.search;

import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.aggregation.AggBuilders;
import com.haizhi.graph.server.api.gdb.search.parser.QueryParser;
import com.haizhi.graph.server.api.gdb.search.query.BoolQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilders;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import com.haizhi.graph.server.arango.search.parser.ArangoTraverseParser;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by chengmo on 2018/1/19.
 */
public class GdbQueryTest {

    @Test
    public void queryParser(){
        GdbQuery query = createGdbQuery();
        System.out.println("########################GdbQuery####################");
        System.out.println(query.toString());

        QueryParser parser = new ArangoTraverseParser();
        XContentBuilder xContent = query.getQuery().toXContent();

        // graphSQL
        String graphSQL = parser.toQuery(xContent);
        System.out.println("########################GraphSQL####################");
        System.out.println(graphSQL);

        // queryExpression
        String queryExpression = parser.toQueryExpression(xContent);
        System.out.println("########################QueryExpression####################");
        System.out.println(queryExpression);
    }

    @Test
    public void queryAndAggregation(){
        GdbQuery query = createGdbQuery();
        System.out.println(query.toString());
    }

    @Test
    public void aggregation(){
        GdbQuery query = new GdbQuery("dev_xl");

        query.addAggBuilder(AggBuilders.term("table1", "field1"));
        query.addAggBuilder(AggBuilders.range("table2", "field2")
                .addRange(100, 200).addUnboundedFrom(300));
        query.addAggBuilder(AggBuilders.range("table2", "field2")
                .addUnboundedTo(500));

        System.out.println(query.toString());
    }

    private GdbQuery createGdbQuery(){
        GdbQuery query = new GdbQuery("dev_xl");

        /* graph builder */
        GraphQBuilder graphBuilder = QBuilders.graphQBuilder(
                Arrays.asList("vertex1", "vertex2"),
                Arrays.asList("edge1", "edge2", "edge3"));
        graphBuilder.startVertices(Arrays.asList("A1", "A2", "A3"));
        graphBuilder.endVertices(Arrays.asList("B1", "B2", "B3"));
        query.setQuery(graphBuilder);

        /* root builder */
        BoolQBuilder rootBuilder = QBuilders.boolQBuilder();
        graphBuilder.setQuery(rootBuilder);

        /* vertex1 builder */
        BoolQBuilder field1AndFiled2 = QBuilders.boolQBuilder();
        field1AndFiled2.must(QBuilders.termQBuilder("field1", "VALUE"));
        field1AndFiled2.must(QBuilders.termQBuilder("field2", "VALUE"));

        BoolQBuilder vertex1 = QBuilders.boolQBuilder();
        vertex1.should(field1AndFiled2);
        vertex1.should(QBuilders.termsQBuilder("field3", "VALUE1", "VALUE2"));
        vertex1.mustNot(QBuilders.termQBuilder("field4", "VALUE"));
        vertex1.mustNot(QBuilders.isNullQBuilder("field5"));
        vertex1.mustNot(QBuilders.likeQBuilder("field6", "%VALUE%"));
        vertex1.mustNot(QBuilders.rangeQBuilder("field7").from(100).to(200));

        /* (edge1 or edge2) builder */
        BoolQBuilder edge1 = QBuilders.boolQBuilder();
        edge1.must(QBuilders.rangeQBuilder("rangeField1").from(100, false).to(200, false));
        edge1.must(QBuilders.rangeQBuilder("rangeField2").from(100).to(200));
        QBuilders.edgeQBuilder("edge1", edge1);

        BoolQBuilder edge2 = QBuilders.boolQBuilder();
        edge2.must(QBuilders.rangeQBuilder("rangeField1").from(300, false));
        edge2.must(QBuilders.rangeQBuilder("rangeField2").to(300, false));

        BoolQBuilder edge1OrEdge2 = QBuilders.boolQBuilder();
        edge1OrEdge2.should(QBuilders.edgeQBuilder("edge1", "e1", edge1));
        edge1OrEdge2.should(QBuilders.edgeQBuilder("edge2", edge2));

        /* vertex1 and (edge1 or edge2) */
        rootBuilder.must(QBuilders.vertexQBuilder("vertex1", "v1", vertex1));
        rootBuilder.must(edge1OrEdge2);

        // aggregation
        query.addAggBuilder(AggBuilders.term("table1", "field1"));
        query.addAggBuilder(AggBuilders.range("table2", "field2")
                .addRange(100, 200).addUnboundedFrom(300));
        query.addAggBuilder(AggBuilders.range("table2", "field2")
                .addUnboundedTo(500));
        return query;
    }
}
