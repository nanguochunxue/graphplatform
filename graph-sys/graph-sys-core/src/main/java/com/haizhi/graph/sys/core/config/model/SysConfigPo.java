package com.haizhi.graph.sys.core.config.model;

import com.haizhi.graph.common.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2019/4/3.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "sys_config")
public class SysConfigPo extends BasePo {

    @NotNull
    @Column(name = "type", length = 128)
    private String type;

    @NotNull
    @Column(name = "sub_type", length = 128)
    private String subType;

    @NotNull
    @Column(name = "key", length = 128)
    private String key;

    @NotNull
    @Column(name = "value", length = 1000)
    private String value;

}
