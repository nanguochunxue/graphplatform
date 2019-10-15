package com.haizhi.graph.dc.inbound.api.service;

import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Create by zhoumingbing on 2019-06-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class VertexEdgeServiceTest {

    @Autowired
    private VertexEdgeService vertexEdgeService;


    @Test
    public void addVertexEdge() {
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex11", "toVertex1", "edge1"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph21", "fromVertex22", "toVertex2", "edge2"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph31", "fromVertex33", "toVertex3", "edge3"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph41", "fromVertex42", "toVertex4", "edge4"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph51", "fromVertex52", "toVertex5", "edge5"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex12", "toVertex1", "edge1"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph21", "fromVertex22", "toVertex2", "edge2"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex12", "toVertex11", "edge3"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex12", "toVertex12", "edge4"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex12", "toVertex13", "edge4"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex12", "toVertex14", "edge4"));
        vertexEdgeService.addVertexEdge(new DcVertexEdgePo("graph11", "fromVertex12", "toVertex15", "edge4"));
    }
}
