package com.haizhi.graph.dc.core.bean;

import com.haizhi.graph.common.constant.FieldType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by chengmo on 2018/1/17.
 */
@Data
@NoArgsConstructor
public class SchemaField implements Serializable {
    private long graphId;
    private String graphName;
    private long schemaId;
    private String schemaName;
    private String field;
    private String fieldCnName;
    private FieldType type;
    private boolean useSearch;
    private boolean useGraphDb;
    private boolean isMain;
    private float searchWeight = 1.0F;

    public SchemaField(String schemaName, String field, String fieldCnName, FieldType type, boolean useSearch) {
        this.schemaName = schemaName;
        this.field = field;
        this.fieldCnName = fieldCnName;
        this.type = type;
        this.useSearch = useSearch;
    }

    public SchemaField(String schemaName, String field, String fieldCnName, FieldType type, boolean useSearch, float
            searchWeight) {
        this.schemaName = schemaName;
        this.field = field;
        this.fieldCnName = fieldCnName;
        this.type = type;
        this.useSearch = useSearch;
        this.searchWeight = searchWeight;
    }
}
