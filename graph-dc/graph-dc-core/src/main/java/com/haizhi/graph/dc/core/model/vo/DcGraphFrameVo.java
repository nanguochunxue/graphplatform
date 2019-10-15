package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/03/26
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "图在左侧需要显示schema总数-DcGraphFrameVo", description = "库信息查询搜索")
public class DcGraphFrameVo extends DcGraphVo {

    @ApiModelProperty(value = "schema总数", example = "19")
    private Long schemaCount;

    public DcGraphFrameVo(DcGraphPo po, Long schemaCount) {
        super(po);
        this.schemaCount = schemaCount;
    }
}
