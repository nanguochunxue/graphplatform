package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.model.suo.DcStoreParamSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by chengangxiong on 2019/05/05
 */
@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "dc_store_param")
@NoArgsConstructor
public class DcStoreParamPo extends BasePo {

    @Column(name = "store_id", length = 11)
    private Long storeId;

    @NotNull
    @Column(name = "`key`", length = 50)
    private String key;

    @NotNull
    @Column(name = "`value`", length = 256)
    private String value;

    public DcStoreParamPo(Long storeId, DcStoreParamSuo dcStoreParamSuo) {
        this.id = dcStoreParamSuo.getId();
        this.storeId = storeId;
        this.key = dcStoreParamSuo.getKey();
        this.value = dcStoreParamSuo.getValue();
    }
}
