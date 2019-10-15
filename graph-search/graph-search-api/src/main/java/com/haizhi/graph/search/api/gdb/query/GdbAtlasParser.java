package com.haizhi.graph.search.api.gdb.query;

import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilders;

import java.util.Set;

/**
 * Created by chengmo on 2019/1/21.
 */
public class GdbAtlasParser {

    public static GdbQuery toGdbQuery(GdbAtlasQo gdbAtlasQo, Set<String> vertexTables) {
        String graph = gdbAtlasQo.getGraph();
        Set<String> edgeTables = gdbAtlasQo.getEdgeTables();
        Set<String> startVertices = gdbAtlasQo.getStartVertices();
        GdbQuery gdbQuery = new GdbQuery(graph);
        GraphQBuilder builder = QBuilders.graphQBuilder(vertexTables, edgeTables);
        builder.startVertices(startVertices);
        builder.endVertices(gdbAtlasQo.getEndVertices());
        builder.direction(gdbAtlasQo.getDirection());
        builder.maxDepth(gdbAtlasQo.getMaxDepth());
        builder.offset(gdbAtlasQo.getOffset());
        builder.size(gdbAtlasQo.getSize());
        gdbQuery.setQuery(builder);
        int maxSize = gdbAtlasQo.getMaxSize();
        gdbQuery.setSize(maxSize > 0 ? maxSize : null);
        // filter
        GdbFilterBuilder.build(gdbAtlasQo.getFilter(), builder);
        return gdbQuery;
    }
}
