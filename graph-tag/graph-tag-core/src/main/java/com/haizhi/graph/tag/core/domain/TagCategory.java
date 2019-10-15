package com.haizhi.graph.tag.core.domain;

import com.haizhi.graph.common.model.BasePo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2018/3/2.
 */
@Data
@Entity
@Table(name = "tag_category")
public class TagCategory extends BasePo {

    private static final long serialVersionUID = -5411945906794930275L;

    @NotNull
    @Column(name = "`graph`", length = 50)
    private String graph;

    @NotNull
    @Column(name = "`tag_category_name`", length = 50)
    private String tagCategoryName;

    @Column(name = "`tag_category_desc`", length = 500)
    private String tagCategoryDesc;

    @Column(name = "parent")
    private long parent;

    @Column(name = "level")
    private int level = 1;

    @Column(name = "is_leaf", columnDefinition = "tinyint(1)")
    private Boolean isLeaf = false;
}
