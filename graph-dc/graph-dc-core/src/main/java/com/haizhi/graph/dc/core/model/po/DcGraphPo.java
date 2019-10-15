package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.model.suo.DcGraphSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2018/8/16.
 */
@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "dc_graph")
@NoArgsConstructor
public class DcGraphPo extends BasePo {

    @NotNull
    @Column(name = "graph", length = 50)
    private String graph;

    @NotNull
    @Column(name = "graph_name_cn", length = 50)
    private String graphNameCn;

    @Column(name = "remark", length = 200)
    private String remark;

    public DcGraphPo(DcGraphSuo suo) {
        this.id = suo.getId();
        this.graph = suo.getGraph();
        this.graphNameCn = suo.getGraphNameCn();
        this.remark = suo.getRemark();
    }
}
