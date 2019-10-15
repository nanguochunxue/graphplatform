package com.haizhi.graph.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haizhi.graph.common.constant.Constants;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chengmo on 2018/5/15.
 */
@Data
@MappedSuperclass
//@EntityListeners(PoEntityListener.class)
public class BasePo implements Serializable {

    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(name = "enabled_flag", length = 1)
    protected String enabledFlag = Constants.Y;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "created_dt", insertable = false, updatable = false)
    protected Date createdDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "updated_dt", insertable = false, updatable = false)
    protected Date updatedDt;

    @Column(name = "created_by", updatable = false, length = 50)
    protected String createdById = "1";

    @Column(name = "updated_by", insertable = false, length = 50)
    protected String updateById = "1";
}
