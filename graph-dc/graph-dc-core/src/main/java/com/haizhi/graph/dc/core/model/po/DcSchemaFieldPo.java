package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaFieldSuo;
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
@Table(name = "dc_schema_field")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DcSchemaFieldPo extends BasePo {

    @NotNull
    @Column(name = "graph", length = 50)
    private String graph;

    @NotNull
    @Column(name = "`schema`", length = 50)
    private String schema;

    @NotNull
    @Column(name = "`field`", length = 50)
    private String field;

    @NotNull
    @Column(name = "`field_name_cn`", length = 50)
    private String fieldNameCn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", length = 30)
    private FieldType type;

    @Column(name = "search_weight")
    private int searchWeight;

    @Column(name = "is_main", columnDefinition = "tinyint(1)")
    private boolean isMain;

    @Column(name = "modifiable", columnDefinition = "tinyint(1)")
    private boolean modifiable;

    @NotNull
    @Column(name = "sequence")
    private int sequence = 0;

    public DcSchemaFieldPo(DcSchemaFieldSuo suo) {
        this.id = suo.getId();
        this.graph = suo.getGraph();
        this.field = suo.getField();
        this.schema = suo.getSchema();
        this.fieldNameCn = suo.getFieldNameCn();
        this.type = suo.getType();
        this.isMain = suo.isMain();
        this.searchWeight = suo.getSearchWeight();
        this.modifiable = true;
    }
}