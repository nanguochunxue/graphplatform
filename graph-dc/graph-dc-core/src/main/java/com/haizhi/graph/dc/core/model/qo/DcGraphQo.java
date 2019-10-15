package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.model.PageQoBase;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengmo on 2018/10/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "元数据-资源库查询对象DcGraphQo", description = "搜索条件")
public class DcGraphQo extends PageQoBase {
}
