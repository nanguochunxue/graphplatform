package com.haizhi.graph.sys.auth.model.suo;

import com.haizhi.graph.sys.auth.constant.SysType;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统角色新增或更新参数SysRoleSuo",description = "用于自定义系统角色新增或更新")
public class SysRoleSuo {

    @ApiModelProperty(value = "角色ID，如果为NULL表示新增", example = "3")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名", example = "普通用户")
    private String name;

    @ApiModelProperty(value = "角色代码", example = "A123")
    private String code;

    @ApiModelProperty(value = "角色备注", example = "Y")
    private String remark;

    @NotNull(message = "系统类型不能为空")
    @ApiModelProperty(value = "系统类型", example = "SYS")
    private SysType type;

    @ApiModelProperty(value = "功能资源树")
    private List<SysResourceVo> resources;


}
