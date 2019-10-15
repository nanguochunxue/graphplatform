package com.haizhi.graph.common.model;

import com.haizhi.graph.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by tanghaiyang on 2018/12/24.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务id", example = "admin")
    private Long id;

    @ApiModelProperty(value = "标记，0-正常，1-冻结", example = "0")
    protected String enabledFlag;

    @ApiModelProperty(value = "创建者", example = "admin")
    protected String createdById;

    @ApiModelProperty(value = "创建时间", example = "2018-12-24 14:31:27")
    protected String createdDt;

    @ApiModelProperty(value = "更新者", example = "haizhi")
    protected String updatedById;

    @ApiModelProperty(value = "更新时间", example = "2018-12-24 14:31:27")
    protected String updatedDt;

    public BaseVo(BasePo po) {
        this.id = po.getId();
        this.enabledFlag = po.getEnabledFlag();
        this.createdById = po.getCreatedById();
        this.updatedById = po.getUpdateById();
        this.createdDt = po.getCreatedDt() == null ? null : DateUtils.formatLocal(po.getCreatedDt());
        this.updatedDt = po.getUpdatedDt() == null ? null : DateUtils.formatLocal(po.getUpdatedDt());
    }

}
