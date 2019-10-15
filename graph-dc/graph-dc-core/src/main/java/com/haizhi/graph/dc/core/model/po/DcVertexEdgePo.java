package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.core.util.Md5Utils;
import com.haizhi.graph.dc.core.model.suo.DcVertexEdgeSuo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2018/8/16.
 */
@Data
@Entity
@Table(name = "dc_vertex_edge")
@NoArgsConstructor
public class DcVertexEdgePo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @NotNull
    @Column(name = "graph", length = 50)
    private String graph;

    @NotNull
    @Column(name = "`from_vertex`", length = 128)
    private String fromVertex;

    @NotNull
    @Column(name = "`to_vertex`", length = 128)
    private String toVertex;

    @NotNull
    @Column(name = "`edge`", length = 128)
    private String edge;

    @NotNull
    @Column(name = "`key`", length = 50)
    private String key;

    public DcVertexEdgePo(DcVertexEdgeSuo suo) {
        this.id = suo.getId();
        this.graph = suo.getGraph();
        this.edge = suo.getEdge();
        this.fromVertex = suo.getFromSchema();
        this.fromVertex = suo.getToSchema();
        this.key = getMd5Key();
    }

    public DcVertexEdgePo(String graph, String fromVertex, String toVertex, String edge) {
        this.graph = graph;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.edge = edge;
        this.key = getMd5Key();
    }

    public String getMd5Key() {
        return Md5Utils.encode(this.graph + this.fromVertex + this.toVertex + this.edge);
    }
}
