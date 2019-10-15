package com.haizhi.graph.tag.core.bean;

import com.haizhi.graph.tag.core.domain.TagSchemaType;
import lombok.Data;

/**
 * Created by chengmo on 2018/3/9.
 */
@Data
public class TagSchemaInfo {
    private long tagId;
    private String graph;
    private TagSchemaType type;
    private String schema;
    private String fields;

    public String getContent() {
        return type.name() + schema + fields;
    }
}
