package com.haizhi.graph.tag.core.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by chengmo on 2018/3/2.
 */
@Data
@Entity
@Table(name = "tag_schema")
public class TagSchema implements Serializable {

    private static final long serialVersionUID = -5411945906794930275L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "tag_id")
    private Long tagId;

    @NotNull
    @Column(name = "`graph`", length = 50)
    private String graph;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", length = 20)
    private TagSchemaType type;

    @NotNull
    @Column(name = "`schema`", length = 60)
    private String schema;

    @Column(name = "`fields`", length = 500)
    private String fields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public TagSchemaType getType() {
        return type;
    }

    public void setType(TagSchemaType type) {
        this.type = type;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
