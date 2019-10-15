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
public class TagDependency implements Serializable {

    private static final long serialVersionUID = -5411945906794930275L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "`graph`", length = 50)
    private String graph;

    @NotNull
    @Column(name = "from_tag_id")
    private Long fromTagId;

    @NotNull
    @Column(name = "to_tag_id")
    private Long toTagId;
}
