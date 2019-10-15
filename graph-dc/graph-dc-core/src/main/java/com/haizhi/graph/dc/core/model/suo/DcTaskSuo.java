package com.haizhi.graph.dc.core.model.suo;

import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.FileTaskSourceType;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Data
@ApiModel(value = "任务新增或更新参数DcTaskSuo", description = "任务新增或更新")
public class DcTaskSuo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "资源库名称", example = "graph_one", required = true)
    private String graph;

    @ApiModelProperty(value = "目标表", example = "schema_one", required = true)
    private String schema;

    @ApiModelProperty(value = "任务名称", example = "task_name", required = true)
    private String taskName;

    @ApiModelProperty(value = "任务类型", example = "HDFS", required = true)
    private TaskType taskType;

    @ApiModelProperty(value = "数据源地址id", example = "12")
    private Long storeId;

    @ApiModelProperty(value = "数据源资源", example = "/data/file/a.txt")
    private String source;

    @ApiModelProperty(value = "本地文件或服务器文件路径", example = "UPLOAD_FILE")
    private FileTaskSourceType sourceType;

    @ApiModelProperty(value = "执行类型", example = "ONCE", required = true)
    private ExecutionType executionType;

    @ApiModelProperty(value = "cron表达式", example = "0/10 * * * * ? ")
    private String cron;

    @ApiModelProperty(value = "操作类型", example = "UPSERT")
    private OperateType operateType;

    @ApiModelProperty(value = "上传完成后立即执行", example = "true", required = true)
    private boolean runImmediately = false;

    @ApiModelProperty(value = "文件ID")
    private List<Long> fileIds;

    @ApiModelProperty(value = "错误处理方式，-1表示出错继续，大于或等于0表示错误行大于该值终止")
    private Integer errorMode = -1;

    @ApiModelProperty(value = "任务字段的映射关系")
    private List<DcTaskMetaSuo> taskMetaSuoList;
}
