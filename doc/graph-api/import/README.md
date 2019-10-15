## 需求&目标

> 通过Shell脚本方式单次执行数据接入中创建的任务，同时监控执行的进度

## 使用示例

- 1，脚本配置

```
配置数据平台URL
cd ../graph-api-3.0.0-SNAPSHOT/bin/
vim dmp-import
URL_GRAPH_API="http://192.168.1.57:10030"
```

- 2，脚本执行

```
脚本默认存放的安装目录
cd ../graph-api-3.0.0-SNAPSHOT/bin/
./dmp-import --task-id 1 --batchStartId 8888 --batchEndId 10000
./dmp-import-stop --task-id 1 
```

- 3，脚本输出

```
-------------TASK INFO----------------
task_id:                 222
task_name:               sue
graph:                   graph_test
schema:                  sue
task_type:               FILE

----------START TASK SUCCESS----------
task_instance_id:        23700
task_state:              RUNNING
total_rows:              10000

---------------PROGRESS---------------
total:100%(10000), gdb:100%(10000)

---------------COMPLETE---------------
elaspe_ time:            11s
task_detail_url:         http://192.168.1.41:5010/#/root/main/dataAccess/taskDetail?taskId=222

```

## 实现思路

```
此shell通过graph-api模块的rest触发任务导入，并且获取任务进度，任务完成或者中断后shell结束
运行入口在脚本最后面的start语句

核心逻辑：
prepare_enviroment              初始化配置
TASK_ID=$1                      配置任务ID,脚本外部传入
api_task_detail_task $TASK_ID   判断task状态
api_task_runOnce $TASK_ID       执行task任务
check_task_state                检查task状态
```

- 1、通过taskId获取task任务信息
```
以192.168.1.57环境，taskId=23为例：

curl -X GET http://192.168.1.57:10030/api/task/detail/task?id=28
{
	"success": true,
	"payload": {
		"data": {
			"id": 28,
			"enabledFlag": "Y",
			"createdById": "1",
			"createdDt": "2019-06-19 11:29:37",
			"updatedById": "",
			"updatedDt": "2019-06-24 12:07:23",
			"graph": "demo_graph",
			"schema": "demo_vertex",
			"taskName": "demo_vertex",
			"taskType": "FILE",
			"taskState": "RUNNING",
			"taskDisplayState": "SUCCESS",
			"storeId": null,
			"source": "18",
			"executionType": "ONCE",
			"operateType": "UPSERT",
			"cron": null,
			"sourceType": "UPLOAD_FILE",
			"createdByName": "超级管理员",
			"shellScript": "curl 'http://host_port/api/task/submit?id=28'",
			"errorMode": -1,
			"filePoList": [{
				"id": 18,
				"enabledFlag": "Y",
				"createdById": "1",
				"createdDt": "2019-06-19 11:29:33",
				"updatedById": "",
				"updatedDt": "2019-06-19 11:29:33",
				"name": "demo_data.json"
			}],
			"taskMetaVos": [],
			"storeName": null,
			"taskInstanceId": 146
		}
	}
}
```

- 2、触发任务
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ \
   "errorMode": -1, \
   "id": 23 \
 }' 'http://192.168.1.57:10030/api/task/runOnce'

{
    "success": true,
    "payload": {
    "data": null
}
```

- 3、获取任务实例详情
```
curl -X GET   http://192.168.1.57:10030/api/task/detail/process?id=28
{
    "success": true,
    "payload": {
        "data": {
            "taskId": null,
            "taskInstanceId": null,
            "totalRows": 0,
            "gdbAffectedRows": 0,
            "gdbAffectedRate": 0,
            "hbaseAffectedRows": 0,
            "hbaseAffectedRate": 0,
            "esAffectedRows": 0,
            "esAffectedRate": 0,
            "totalAffectedRows": 0,
            "totalRate": 0,
            "taskDisplayState": "READY"
        }
    }
}
```

## 脚本打包

```
脚本采用[jq](https://stedolan.github.io/jq/)解析`graph-api`返回的`json`数据
mac和linux有不同的binary可执行文件
打包命令

URL_GRAPH_API="http://192.168.1.57:10030"
DIR_TMP="/tmp"
JQ="${DIR_TMP}/jq"
sed -n -e '1,/^exit 0$/!p' $0 > "${JQ}" 2>/dev/null
chmod +x $JQ

./build.sh
执行后会生成`import.sh`文件
```