package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.dc.core.model.po.DcStorePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengangxiong on 2019/04/08
 */
@Data
@ApiModel(value = "数据源selector对象DcStoreSelectorVo", description = "资源库管理")
public class DcStoreSelectorVo {

    @ApiModelProperty(value = "id", example = "3")
    private Long id;

    @ApiModelProperty(value = "数据源显示名称；名称【空格】版本", example = "es 5.4.2")
    private String displayName;

    public DcStoreSelectorVo(DcStorePo dcStorePo, String version) {
        this.id = dcStorePo.getId();
        if (!StringUtils.isEmpty(version)){
            this.displayName = dcStorePo.getName() + " " + version;
        }else {
            this.displayName = dcStorePo.getName();
        }
    }
}
