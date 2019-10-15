package com.haizhi.graph.sys.file.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/01/31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ApiModel(value = "文件显示信息SysFileVo", description = "")
public class SysFileVo extends BaseVo {

    @ApiModelProperty(value = "文件名", example = "a.txt")
    private String name;

    public SysFileVo(SysFilePo po){
        super(po);
        this.name = po.getName();
    }
}
