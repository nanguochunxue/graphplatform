## 需求&目标
> 数据接入统一接口

## 实现思路



## 接口设计
swagger地址：
http://192.168.1.101:10010/dc/inbound/swagger-ui.html#/api-inbound-controller

参数说明(都必须)：
graph: 后端数据源的名字，例如hbase的表空间，arangodb的数据库名，es的index名
schema: 后端数据源的表名，例如hbase的表，arangdb的顶点或者边表，es的type名
rows： 具体数据，List<Map>类型
operation： 具体操作，例如CREATE_OR_UPDATE表示插入或者更新
header： 当前导入的实例信息，用于标识和错误处理(测试接口时，header字段可以任意指定，不影响导入功能和性能


```
curl -X POST http://192.168.1.101:10010/dc/inbound/api/bulk -d '
{
	"graph":"test",
    "schema":"Company",
	"rows":[
		{
			"long_field":"3381",
			"object_key":"2RrGkyfy1",
			"double_field":"884.33",
			"date_field":"2019-05-21"
		},
		{
			"long_field":"3381",
			"object_key":"2RrGkyfy1",
			"double_field":"884.33",
			"date_field":"2019-05-21"
		}
	],
    "operation":"CREATE_OR_UPDATE",
	"header":{
        "options":{
            "taskInstanceId":27925,
            "taskId":141
        },
        "type":"API"
    }
}
'
```

