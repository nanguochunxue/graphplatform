## 需求&目标
> 


## 实现思路
> 

- 数据模型（概念）

```
组件=item=itemCode<前后端识别>
槽接口类型SoltType=DataSet,JSON

Flow=>分布式工作流=>airflow.DAG=>场景

Node=>节点=>airflow.TASK=>组件

Edge=>边=>airflow.upStream|downstream=>有向连接

Model=>模型=>Flow.归档

```

- Flow定义

```
output.hdfs="/haizhi/atlas/flow/${dagId}/${nodeId}/output.json"
{
    "id": "UUID",
    "name": "",
    "remark": "",
    "nodes": [
        {
            "id": "UUID",
            "name": "",
            "remark": "",
            "itemCode": "FILE_LOADER",
            "params": {
                "fieldName": "fieldValue"
            },
            "style": {
                "width": 120,
                "height": 36,
                "x": 338,
                "y": 129
            },
            "inSlots": [
                {
                    "id": "UUID",
                    "nodeId": "UUID",
                    "style": {},
                    "slotType": "",
                }
            ],
            "outSlots": [],
            "actions": {
                "showResult": {},
                "saveModel": {},
                "exportModel": {}
            } 
        }
    ],
    "edges": [
        {
            "id": "UUID",
            "name": "",
            "remark": "",
            "fromNode": "UUID",
            "toNode": "UUID",
            "fromSlot": "UUID",
            "toSlot": "UUID"
        }
    ]
}
```

- Model定义：由Flow归档archive

```
Flow模板->Flow保存->Flow归档为Model
nodes，edges的格式与Flow一致
{
    "id": "UUID",
    "flowId": "UUID",
    "name": "",
    "remark": "",
    "nodes": [],
    "edges": []
}
```

- 运行时定义

```
Flow|Model=>FlowInstance|ModelInstance->airflow.dag.instance

state[1-ready-待运行,2-running-运行中,3-succeeded-运行成功,4-failed-运行失败]
{
    "id": "UUID",
    "dagType": "FLOW|MODEL",
    "dagId": "UUID",
    "dagRunId": "2019-07-16T06:45:03+00:00",
    "state": "",
    "startTime": "YYYY-MM-DD HH:mm:ss",
    "endTime": "",
    "nodes": [
        {
            "id": "UUID",
            "dagTaskId": "${itemCode}_UUID",
            "state": "",
            "startTime": "YYYY-MM-DD HH:mm:ss",
            "endTime": "",
            "message": ""
        }
    ]
}


```

- 组件算法参数定义

```
## 步骤
第一步：页面生成Flow
第二步：后台生成Dag.py
    1，构建task，输入，输出参数
    2，构建task之间的依赖
第三步：运行Dag
```

```
dagTaskId=${itemCode}_UUID
{
    "${dagTaskId}":{
        "params": {
            "fieldName": "fieldValue",
            "param1": "value1"
        },
        "input": [
            {
                "slotId": "UUID",
                "edgeId": "UUID",
                "filePath": "",
                "param1": "value1"
            }
        ]
        "output": [
            {
                "slotId": "UUID",
                "edgeId": "UUID",
                "filePath": "",
                "param1": "value1"
            }
        ]
    },
}
```

- 数据库设计

```
1, afl_flow
id,flow_id,name,remark,nodes,edges,sequence

2, afl_model
id,model_id,flow_id,name,remark,nodes,edges

3, afl_dag
id,dag_path,dag_config

4, afl_dag_instance
id,dag_type,dag_id,dag_run_id,nodes
dag_type=[FLOW,MODEL]
----------nodes----------
{
    "UUID": {
        "dagTaskId": "${itemCode}_UUID"
    }
}

5, afl_group
id,name,type
type=[MODEL|ITEM]

6, afl_group_rel
id,group_id,rel_id

7，afl_item
id,item_code,item_name,sequence

8, afl_item_param
id,item_id,param_key,param_value

```

## 接口设计

- 模块设计

```
## 2.0
grahp|dmp、gap、dap、dip、sys、atlas
atlas-ui


## 3.0
知识图谱平台  KnowledgeGraph，Haizhi-KGP，kgp    
    知识构建     KBP
    知识推理     KIP  Knowledge Inference        
    知识分析     KAP        
    知识服务     KSP
    知识应用     KPP   knowledge practice platform 
graph，gap，kip，sys，atlas  
kbp，kip，kap，ksp，kpp
```

``` 
com.haizhi.atlasdb
com.haizhi.atlas.flow
|—atlas-flow
    |-atlas-flow-common
        |-com.haizhi.atlas.flow.common
            |-  <gap-common的内容>
    |-atlas-flow-api
        |-com.haizhi.atlas.flow.api
            |-FlowService.java
    |-atlas-flow-restapi
        |-com.haizhi.atlas.flow.restapi.controller
            |-FlowController.java
    |-atlas-flow-airflow
        |-com.haizhi.atlas.flow.airflow
            |-AirFlowServiceImpl.java
|-kip
    |-kip-common
    |-kip-restapi
    |-kip-sys
    


|-kip-tag
    |-kip-tag-common
        |-com.haizhi.kip.tag.common
            |- <gap-common的内容>
    |-kip-tag-restapi
        |-com.haizhi.kip.tag.restapi
            |-TagController.java
    |-kip-tag-analytics
        |-com.haizhi.kip.tag.analytics
            |-flow
            |-dao
            |-model
            |-service
实现思路            
数据库设计   
接口设计
```