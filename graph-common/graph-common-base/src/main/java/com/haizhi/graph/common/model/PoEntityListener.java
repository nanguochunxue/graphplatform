package com.haizhi.graph.common.model;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Created by chengangxiong on 2019/01/09
 */
public class PoEntityListener {

    @PreUpdate
    protected void PreUpdate(BasePo entity) {
        entity.setUpdateById("1");
    }

    @PrePersist
    protected void prePersist(BasePo entity) {
        entity.setCreatedById("2");
    }
}
