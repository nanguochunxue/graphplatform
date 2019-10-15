package com.haizhi.graph.dc.core.model.suo;

import com.haizhi.graph.common.constant.StoreType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Data
@ApiModel(value = "数据源对象DcGraphSchemaSuo", description = "")
public class DcStoreSuo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "数据源名称", example = "hbase_store")
    private String name;

    @ApiModelProperty(value = "数据源类型", example = "Hbase")
    private StoreType type;

    @ApiModelProperty(value = "环境ID", example = "")
    private Long envId;

    @ApiModelProperty(value = "版本ID", example = "")
    private Long versionDictId;

    @ApiModelProperty(value = "用户", example = "user")
    private String user;

    @ApiModelProperty(value = "密码", example = "111@qq")
    private String password;

    @ApiModelProperty(value = "存储对应的url", example = "http://127.0.0.1:9200")
    private String url;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注")
    private List<DcStoreParamSuo> dcStoreParamList;

    @ApiModelProperty(value = "超时时间")
    private long timeout;
}
