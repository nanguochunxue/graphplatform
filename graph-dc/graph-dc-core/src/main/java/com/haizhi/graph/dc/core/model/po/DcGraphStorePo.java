package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.model.suo.DcGraphStoreSuo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2018/8/16.
 */
@Data
@Entity
@Table(name = "dc_graph_store")
@NoArgsConstructor
public class DcGraphStorePo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @NotNull
    @Column(name = "`graph`", length = 50)
    private String graph;

    @NotNull
    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "store_type", length = 30)
    private StoreType storeType;

    public DcGraphStorePo(DcGraphStoreSuo suo) {
        this.id = suo.getId();
        this.graph = suo.getGraph();
        this.storeId = suo.getStoreId();
        this.storeType = suo.getStoreType();
    }
}
