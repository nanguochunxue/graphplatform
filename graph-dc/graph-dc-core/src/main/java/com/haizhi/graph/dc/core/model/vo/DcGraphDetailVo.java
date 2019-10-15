package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/04/10
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "查询资源库信息-DcGraphFrameVo", description = "库信息查询搜索")
public class DcGraphDetailVo extends BaseVo {

    @ApiModelProperty(value = "资源库名称", example = "graph_one", required = true)
    private String graph;

    @ApiModelProperty(value = "资源库中文名称", example = "图名称", required = true)
    private String graphNameCn;

    @ApiModelProperty(value = "资源库备注", example = "remark")
    private String remark;

    @ApiModelProperty(value = "hbase地址", example = "1", required = true)
    private Long hbase;

    @ApiModelProperty(value = "es地址", example = "2", required = true)
    private Long es;

    @ApiModelProperty(value = "gdb地址", example = "3", required = true)
    private Long gdb;

    @ApiModelProperty(value = "库和资源显示信息", required = true)
    private DcGraphStoreVo graphStoreVo;

    public DcGraphDetailVo(DcGraphPo po, Long hbase, Long es, Long gdb) {
        super(po);
        this.graph = po.getGraph();
        this.graphNameCn = po.getGraphNameCn();
        this.remark = po.getRemark();
        this.hbase = hbase;
        this.es = es;
        this.gdb = gdb;
    }
}
