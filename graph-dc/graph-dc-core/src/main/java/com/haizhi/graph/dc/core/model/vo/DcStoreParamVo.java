package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcStoreParamPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/05/05
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "数据源参数显示对象DcStoreParamVo", description = "数据源-参数显示")
public class DcStoreParamVo extends BaseVo {

    @ApiModelProperty(value = "key值", example = "key_test")
    private String key;

    @ApiModelProperty(value = "value值", example = "value_test")
    private String value;

    public DcStoreParamVo(DcStoreParamPo dcStoreParamPo) {
        super(dcStoreParamPo);
        this.key = dcStoreParamPo.getKey();
        this.value = dcStoreParamPo.getValue();
    }
}
