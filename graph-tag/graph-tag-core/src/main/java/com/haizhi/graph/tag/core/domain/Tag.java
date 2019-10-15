package com.haizhi.graph.tag.core.domain;

import com.haizhi.graph.common.model.BasePo;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by chengmo on 2018/3/2.
 */
@Data
@Entity
@Table(name = "tag")
public class Tag extends BasePo {

    private static final long serialVersionUID = -5411945906794930275L;

    @NotNull
    @Column(name = "`graph`", length = 50)
    private String graph;

    @NotNull
    @Column(name = "tag_category_id")
    private Long tagCategoryId;

    @NotNull
    @Column(name = "`tag_name`", length = 50)
    private String tagName;

    @Column(name = "`tag_desc`", length = 500)
    private String tagDesc;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "`tag_group`", length = 20)
    private TagGroup tagGroup = TagGroup.SYSTEM;

    @Enumerated(EnumType.STRING)
    @Column(name = "`tag_status`", length = 20)
    private TagStatus tagStatus = TagStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "`tag_status_bak`", length = 20)
    private TagStatus tagStatusBak = TagStatus.CREATED;

    @Column(name = "up_time")
    private Date upTime;

    @Column(name = "apply_up_time")
    private Date applyUpTime;

    @Column(name = "apply_by")
    private String applyBy;

    @Column(name = "approve_by")
    private String approveBy;

    @Column(name = "weight")
    private Integer weight = 1;

    @NotNull
    @Column(name = "`source_type`", length = 20)
    private String sourceType = "1";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "`analytics_mode`", length = 20)
    private AnalyticsMode analyticsMode = AnalyticsMode.LOGIC;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "`data_type`", length = 20)
    private DataType dataType = DataType.BOOLEAN;

    @Column(name = "`default_value`", length = 50)
    private String defaultValue;

    @Column(name = "`value_schema`", length = 50)
    private String valueSchema;

    @Column(name = "`value_options`", length = 500)
    private String valueOptions;

    @Column(name = "value_options_enabled", columnDefinition = "tinyint(1)")
    private Boolean valueOptionsEnabled = true;

    @Column(name = "value_history_enabled", columnDefinition = "tinyint(1)")
    private Boolean valueHistoryEnabled = false;

    @Column(name = "`original_rule`", length = 2000)
    private String originalRule;

    @Column(name = "`rule`", length = 2000)
    private String rule;

    @Column(name = "`rule_script`", length = 2000)
    private String ruleScript;
}
