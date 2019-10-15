package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.model.suo.DcEnvSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by chengangxiong on 2019/03/22
 */
@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "dc_env")
@NoArgsConstructor
public class DcEnvPo extends BasePo {

    @NotNull
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "user", length = 50)
    private String user;

    @Column(name = "security_enabled", length = 1)
    private String securityEnabled;

    @Column(name = "remark", length = 200)
    private String remark;

    @NotNull
    @Column(name = "version_dict_id", length = 11)
    private Long versionDictId;

    public DcEnvPo(DcEnvSuo suo) {
        this.id = suo.getId();
        this.name = suo.getName();
        this.versionDictId = suo.getVersionDictId();
        this.user = suo.getUser();
        this.remark = suo.getRemark();
        if (suo.isSecurityEnabled()){
            securityEnabled = Constants.Y;
        }else {
            securityEnabled = Constants.N;
        }
    }
}
