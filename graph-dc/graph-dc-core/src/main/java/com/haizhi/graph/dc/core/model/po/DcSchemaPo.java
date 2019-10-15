package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2018/8/16.
 */
@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "dc_schema")
@NoArgsConstructor
public class DcSchemaPo extends BasePo {

    @NotNull
    @Column(name = "`graph`", length = 128)
    private String graph;

    @NotNull
    @Column(name = "`schema`", length = 128)
    private String schema;

    @NotNull
    @Column(name = "schema_name_cn", length = 128)
    private String schemaNameCn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private SchemaType type;

    @Column(name = "search_weight")
    private int searchWeight;

    @Column(name = "use_search", columnDefinition = "tinyint(1)")
    private boolean useSearch;

    @Column(name = "use_gdb", columnDefinition = "tinyint(1)")
    private boolean useGdb = true;

    @Column(name = "use_hbase", columnDefinition = "tinyint(1)")
    private boolean useHBase = true;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "modifiable", columnDefinition = "tinyint(1)")
    private boolean modifiable;

    @Column(name = "directed", length = 1)
    private String directed;

//    @NotNull
    @Column(name = "sequence")
    private int sequence;

    public DcSchemaPo(DcSchemaSuo suo) {
        this.id = suo.getId();
        this.graph = suo.getGraph();
        this.schema = suo.getSchema();
        this.schemaNameCn = suo.getSchemaNameCn();
        this.type = suo.getType();
        this.useGdb = suo.isUseGdb();
        this.useSearch = suo.isUseSearch();
        this.sequence = suo.getSequence();
        this.searchWeight = suo.getSearchWeight();
        this.remark = suo.getRemark();
        this.useGdb = suo.isUseGdb();
        this.useHBase = suo.isUseHBase();
        this.useSearch = suo.isUseSearch();
        this.directed = suo.isDirected() ? Constants.Y : Constants.N;
    }
}
