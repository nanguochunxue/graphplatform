package com.haizhi.graph.tag.core.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by chengmo on 2018/5/2.
 */
@Data
@Entity
@Table(name = "tag_parameter")
public class TagParameter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "`graph`", length = 50)
    private String graph;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", length = 20)
    private TagParameterType type;

    @NotNull
    @Column(name = "`name`", length = 100)
    private String name;

    @NotNull
    @Column(name = "`reference`", length = 2000)
    private String reference;

    @NotNull
    @Column(name = "`value`", length = 1000)
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public TagParameterType getType() {
        return type;
    }

    public void setType(TagParameterType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
