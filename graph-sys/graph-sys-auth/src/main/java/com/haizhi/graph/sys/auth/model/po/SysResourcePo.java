package com.haizhi.graph.sys.auth.model.po;

import com.haizhi.graph.common.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_resource")
public class SysResourcePo extends BasePo {

    @Column(name = "name")
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "url")
    private String url;

    @Column(name = "group")
    private String group;

    @Column(name = "code")
    private String code;

    @Column(name = "remark")
    private String remark;

    @Column(name = "type")
    private String type;

    @Column(name = "sub_type")
    private String subType;

}
