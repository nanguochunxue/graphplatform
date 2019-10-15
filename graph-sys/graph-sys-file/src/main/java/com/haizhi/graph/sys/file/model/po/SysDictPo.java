package com.haizhi.graph.sys.file.model.po;

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
 * Created by chengangxiong on 2019/03/26
 */
@Data
@Entity
@Table(name = "sys_dict")
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysDictPo extends BasePo {

    @NotNull
    @Column(name = "key", length = 50)
    private String key;

    @NotNull
    @Column(name = "value", length = 200)
    private String value;

    @NotNull
    @Column(name = "parentId", length = 11)
    private Long parentId;
}
