package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chengmo on 2018/8/16.
 */
@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "dc_store")
@NoArgsConstructor
public class DcStorePo extends BasePo {

    @NotNull
    @Column(name = "name", length = 50)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private StoreType type;

    @NotNull
    @Column(name = "`url`", length = 128)
    private String url;

    @Column(name = "env_id")
    private Long envId;

    @NotNull
    @Column(name = "`user`", length = 50)
    private String user;

    @NotNull
    @Column(name = "`password`", length = 128)
    private String password;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "version_dict_id", length = 11)
    private Long versionDictId;

    public DcStorePo(DcStoreSuo suo){
        this.id = suo.getId();
        this.name = suo.getName();
        this.envId = suo.getEnvId();
        this.user = suo.getUser();
        this.password = suo.getPassword();
        this.type = suo.getType();
        this.url = suo.getUrl();
        this.remark = suo.getRemark();
        this.versionDictId = suo.getVersionDictId();
    }
}
