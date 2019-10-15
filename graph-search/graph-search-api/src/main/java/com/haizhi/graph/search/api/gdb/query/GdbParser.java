package com.haizhi.graph.search.api.gdb.query;

import com.haizhi.graph.common.model.BaseQo;
import com.haizhi.graph.search.api.model.qo.GdbSearchQo;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilders;

import java.util.Objects;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/1/21.
 */
public class GdbParser {

    public static GdbQuery toGdbQuery(GdbSearchQo searchQo, Set<String> vertexTablesRaw) {
        if (Objects.isNull(searchQo.getType())){
            throw new IllegalArgumentException("type cannot be null");
        }
        String graph = searchQo.getGraph();
//        Set<String> vertexTables = searchQo.getVertexTables();      //it is deprecated because use mysql table: dc_vertex_edge
        Set<String> edgeTables = searchQo.getEdgeTables();
        Set<String> startVertices = searchQo.getStartVertices();
        GdbQuery gdbQuery = new GdbQuery(graph);
        GraphQBuilder builder = QBuilders.graphQBuilder(vertexTablesRaw, edgeTables);
        builder.startVertices(startVertices);
        builder.endVertices(searchQo.getEndVertices());
        builder.direction(searchQo.getDirection().name());
        builder.maxDepth(searchQo.getMaxDepth());
        builder.offset(searchQo.getOffset());
        builder.size(searchQo.getSize());
        gdbQuery.setQueryBuilder(builder);

        int maxSize = searchQo.getMaxSize();
        gdbQuery.setSize(maxSize > 0 ? maxSize : null);
        int timeout = searchQo.getInternalOption().getIntValue(BaseQo.TIMEOUT);
        boolean isDebug = searchQo.getInternalOption().getBooleanValue(BaseQo.DEBUG_FIELD);
        if(timeout>0) {
            gdbQuery.setTimeout(timeout);
        }
        gdbQuery.setDebugEnabled(isDebug);
        GdbFilterBuilder.build(searchQo.getFilter(), builder);      // build filter
        return gdbQuery;
    }
}
