package com.haizhi.graph.sys.auth.model.po;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@Entity
@Table(name = "sys_user_role")
public class SysUserRolePo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;

}
