package com.haizhi.graph.dc.core.model.vo;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.FileTaskSourceType;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.sys.file.model.vo.SysFileVo;
import com.querydsl.core.Tuple;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ApiModel(value = "任务显示对象DcTaskVo", description = "用于展示任务信息")
public class DcTaskVo extends BaseVo {

    @ApiModelProperty(value = "资源库名称", example = "graph_one", required = true)
    private String graph;

    @ApiModelProperty(value = "目标表", example = "schema_one", required = true)
    private String schema;

    @ApiModelProperty(value = "任务名称", example = "task_name", required = true)
    private String taskName;

    @ApiModelProperty(value = "任务类型", example = "HDFS", required = true)
    private TaskType taskType;

    @ApiModelProperty(value = "任务状态", example = "RUNNING", required = true)
    private String taskState;

    @ApiModelProperty(value = "任务实例状态", example = "RUNNING", required = true)
    private String taskDisplayState;

    @ApiModelProperty(value = "数据源地址id", example = "12")
    private Long storeId;

    @ApiModelProperty(value = "数据源资源", example = "/data/file/a.txt")
    private String source;

    @ApiModelProperty(value = "执行类型", example = "ONCE", required = true)
    private ExecutionType executionType;

    @ApiModelProperty(value = "操作类型", example = "UPSERT", required = true)
    private OperateType operateType;

    @ApiModelProperty(value = "cron表达式", example = "0/10 * * * * ? ")
    private String cron;

    @ApiModelProperty(value = "本地文件或服务器文件路径", example = "UPLOAD_FILE")
    private FileTaskSourceType sourceType;

    @ApiModelProperty(value = "创建人名字", example = "管理员A")
    protected String createdByName = "不变的测试数据"; // FIXME

    @ApiModelProperty(value = "shell脚本", example = "管理员A")
    protected String shellScript = "curl http://xxxxx";

    @ApiModelProperty(value = "错误处理方式，-1表示出错继续，大于或等于0表示错误行大于该值终止")
    private Integer errorMode;

    @ApiModelProperty(value = "文件列表")
    protected List<SysFileVo> filePoList;

    @ApiModelProperty(value = "映射关系")
    protected List<DcTaskMetaVo> taskMetaVos;

    @ApiModelProperty(value = "数据源配置表名称", example = "gp_name")
    private String storeName;

    @ApiModelProperty(value = "最新任务实例ID", example = "1")
    private Long taskInstanceId;

    public DcTaskVo(DcTaskPo dcTaskPo) {
        super(dcTaskPo);
        this.graph = dcTaskPo.getGraph();
        this.schema = dcTaskPo.getSchema();
        this.taskName = dcTaskPo.getTaskName();
        this.taskType = dcTaskPo.getTaskType();
        this.taskState = dcTaskPo.getTaskState().name();
        this.storeId = dcTaskPo.getStoreId();
        this.source = dcTaskPo.getSource();
        this.executionType = dcTaskPo.getExecutionType();
        this.cron = dcTaskPo.getCron();
        this.operateType = dcTaskPo.getOperateType();
        this.errorMode = dcTaskPo.getErrorMode();
        this.taskInstanceId = dcTaskPo.getLastInstanceId();
        if (taskType == TaskType.FILE) {
            Map<String, String> sourceMap = JSON.parseObject(this.source, Map.class);
            String sourceTypeStr = sourceMap.keySet().iterator().next();
            sourceType = FileTaskSourceType.valueOf(sourceTypeStr);
            this.source = sourceMap.get(sourceTypeStr);
        }
    }

    public DcTaskVo(Tuple result) {
    }
}
