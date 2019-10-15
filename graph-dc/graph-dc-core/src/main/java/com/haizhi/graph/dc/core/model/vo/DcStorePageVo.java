package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "数据源查询对象DcStorePageVo", description = "资源库管理")
public class DcStorePageVo extends BaseVo {

    @ApiModelProperty(value = "数据源名称", example = "store_es_test")
    private String name;

    @ApiModelProperty(value = "数据源类型", example = "ES")
    private StoreType type;

    @ApiModelProperty(value = "数据源版本", example = "es6.0")
    private String version;

    @ApiModelProperty(value = "大数据环境", example = "HDP5.0")
    private String env;

    @ApiModelProperty(value = "存储对应的url", example = "http://127.0.0.1:9200")
    private String url;

    @ApiModelProperty(value = "备注", example = "remark")
    private String remark;

    @ApiModelProperty(value = "用户名", example = "admin")
    private String user;

    @ApiModelProperty(value = "密码", example = "admin")
    private String password;

    @ApiModelProperty(value = "创建人", example = "测试人员A")
    protected String createdByName;

    public DcStorePageVo(DcStorePo po) {
        super(po);
        this.name = po.getName();
        this.type = po.getType();
        this.url = po.getUrl();
        this.remark = po.getRemark();
    }

    public DcStorePageVo(DcStorePo po, String createdByName, String env, String version) {
        this(po);
        this.createdByName = createdByName;
        this.env = env;
        this.version = version;
    }
}
