package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/03/11
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "元数据-资源库名称校验对象DcGraphCheckQO", description = "校验参数")
public class DcGraphCheckQO extends DcNameCheckQO{
}
