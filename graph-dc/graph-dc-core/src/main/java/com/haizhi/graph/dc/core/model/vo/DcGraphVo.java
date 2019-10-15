package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengmo on 2018/10/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "图信息查询对象DcGraphVo", description = "库信息查询搜索")
public class DcGraphVo extends BaseVo {

    @ApiModelProperty(value = "图名称", example = "graph_one")
    private String graph;

    @ApiModelProperty(value = "图中文名称", example = "图名称")
    private String graphNameCn;

    @ApiModelProperty(value = "图备注", example = "remark")
    private String remark;

    public DcGraphVo(DcGraphPo po) {
        super(po);
        this.graph = po.getGraph();
        this.graphNameCn = po.getGraphNameCn();
        this.remark = po.getRemark();
    }
}
