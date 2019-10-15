package com.haizhi.graph.search.arango.service;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.search.api.gdb.result.GdbResultBuilder;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.search.arango.Application;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchDao;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilders;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeBuilder;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Created by chengmo on 2018/6/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "")
public class GdbSearchTest {

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private GdbSearchDao gdbSearchDao;

    @Test
    public void traverse(){
        GdbAtlasQo searchQo = FileUtils.readJSONObject("atlas/8_api_2.json", GdbAtlasQo.class);

        String graph = searchQo.getGraph();
        Domain domain = dcMetadataCache.getDomain(graph);
        Set<String> edgeTables = searchQo.getEdgeTables();
        Set<String> vertexTables = domain.getRelatedVertexNames(searchQo.getEdgeTables());
        Set<String> startVertices = searchQo.getStartVertices();

        // traverse
        GdbQuery gdbQuery = new GdbQuery(graph);
        GraphQBuilder builder = QBuilders.graphQBuilder(vertexTables, edgeTables);
        builder.startVertices(startVertices);
        builder.endVertices(searchQo.getEndVertices());
        builder.direction(searchQo.getDirection());
        builder.maxDepth(searchQo.getMaxDepth());
        builder.offset(searchQo.getOffset());
        builder.size(searchQo.getSize());
//        gdbQuery.setQueryBuilder(builder);
        int maxSize = searchQo.getMaxSize();
        gdbQuery.setSize(maxSize > 0 ? maxSize : null);
        GdbQueryResult gdbResult = gdbSearchDao.traverse(gdbQuery);

        String rootVertex = startVertices.iterator().next();
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertex, gdbResult.getData(),
//                "Person/L3KFL54944A834757515A1D521C6854F");
                "vertex/2742789");
        JSONUtils.println(root);
        System.out.println("********************");
        GdbResultBuilder.rebuild(searchQo, root);
        JSONUtils.println(root);
    }
}
