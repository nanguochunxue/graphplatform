## 需求&目标
> 模拟文件导入后端的测试

## 实现思路

- 1.1 初始化测试脚本 ./init.SQL
    - 1.1.1 清空相关数据
    - 1.1.2 初始化相关数据

- 1.2 启动本地服务
     graph-api              // 前端
     graph-dc-store         // 前端
     graph-dc-inbound-api   // 后端数据导入接口

     graph-dc-arango        // 后端消费-入库arango
     graph-dc-hbase         // 后端消费-入库hbase
     graph-dc-es            // 后端消费-入库hbase

- 1.3 测试数据准备

    ```
    测试数据文件 example.json
    ```

- 1.3 执行文件导入任务

    ```
    # SwaggerUi
    http://localhost:10030/api/swagger-ui.html#!/task-inbound-controller/runOnceUsingPOST

    curl -X GET --header 'Accept: application/json' 'http://localhost:10030/api/task/stop?id=150000'

    # RestApi
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ \
         "id": 150000, \
         "errorMode": -1 \
     }' 'http://localhost:10030/api/task/runOnce'
    ```

- 1.4 数据清理
```
清理es：
curl -X DELETE http://192.168.1.49:9200/test

清理arango:
curl -X POST http://192.168.1.176:8529/_db/graph500/_api/cursor -d '
{
    "id": "currentFrontendQuery",
    "query": "FOR v IN Company FILTER v.date_field >0 REMOVE v in Company",
    "options": {
        "profile": false
    },
    "batchSize": 10,
    "cache": true
}
'

清理hbase:
drop 'Company'
drop_namespace 'test'
```

- 开发测试数据规范

```
# 环境
demo_cdh5.7.0
demo_fic80
demo_ksyun

# 数据源
demo_es_5_x
    
demo_es_6_x_fi
192.168.1.223:24148,192.168.1.224:24148,192.168.1.225:24148

demo_es_6_x_ksyun
192.168.1.190:9200

demo_atlas_1_0
192.168.1.176:8529
dmp_manager  dmp_manager@2019

demo_greenplum
    jdbc:postgresql://192.168.1.213:5432/demo_graph     gpadmin
demo_hbase
demo_hbase_fic80
demo_hbase_ksyun

demo_hive
demo_hive_fic80

demo_hdfs
hdfs://192.168.1.16:8022

demo_hdfs_fic80

demo_atlas

# 元数据
demo_graph
    demo_vertex
        object_key
        demo_string_field
        demo_long_field
        demo_double_field
        demo_date_field
    demo_edge
        object_key
        from_key
        to_key
        demo_string_field
        demo_long_field
        demo_double_field
        demo_date_field

# 接入任务
demo_vertex_file
demo_vertex_gp
select object_key,demo_string_field,demo_long_field,demo_double_field,demo_date_field from demo_vertex where demo_long_field<8188

```
