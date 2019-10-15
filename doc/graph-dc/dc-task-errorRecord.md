## 需求&目标
> 数据接入错误记录的处理、存储、查询

## 实现思路

- 按阶段处理
    - 1，读取数据发送Kafka
        - 1.1 读取数据的出错当故障处理，直接结束任务，返回故障原因，如：Spark任务执行初始化失败等
        - 1.2 发送Kafka前的校验出错，需要记录`数据校验失败`
    - 2，消费Kafka入库
        - 2.1 GDB入库失败，需要记录`入库失败`
            - 2.1.1 一般错误，重试机制(默认3次)
            - 2.1.2 如果服务器连接不上或宕机当故障处理
        - 2.2 ES入库失败，需要记录`入库失败`
            - 2.1.1 一般错误，重试机制(默认3次)
            - 2.1.2 如果服务器连接不上或宕机当故障处理
        - 2.3 HBase入库失败，需要记录`入库失败`
            - 2.1.1 一般错误，重试机制(默认3次)
            - 2.1.2 如果服务器连接不上或宕机当故障处理

- 故障处理机制
    - 1，记录Error日志
    - 2，预警(暂不实现)
    
- 错误处理机制
    - 1，消费kafka，需要验证`错误数量是否达到设定的errorMode`,来决定是否往下执行
        - 1.1 数据验证失败，将错误数量汇总
        - 1.2 数据入库失败
            - 1.2.1 GDB入库错误，将错误数量汇总
            - 1.2.2 ES入库错误，将错误数量汇总
            - 1.2.3 ES入库错误，将错误数量汇总
    - 2，Redis统计汇总信息
        - 2.1 key设计规则`com:haizhi:graph:dc:${taskInstanceId}:${storeType}` 

- 存储与查询
    - 1，采用ES或日志文件记录的方式，默认为ES启动可配，如果是日志文件需要关闭前端错误记录的查看权限
    - 2，ES方式
        - 2.1 库名设计
            2.1.1 ES6:`GRAPH.SCHEMA`，如：ccb_dev.company_error  Index必须小写不然报错
            2.1.1 ES:`GRAPH.SCHEMA`，如：ccb_dev.error  Index必须小写不然报错
        - 2.2 表名设计
            2.1.1 ES6:`SCHEMA_error`，如：Company_error
            2.1.1 ES:`SCHEMA_error`，如：Company_error
        - 2.3 表设计，id(object_key+affected_store),object_key,task_instance_id,error_type,error_log,affected_store,data_row,updated_dt
    - 3，文件方式：保存ES表结构的CSV文件格式
        - 3.1 文件名设计，`GRAPH.error.SCHEMA.TASK_INSTANCE_ID`，如：ccb_dev.error.Company.1
        - 
    - 4，存储上限：如果单表超过1万的记录不保存   
     
- 查询示例
```
## swagger-ui 地址
graph-search-es模块的
http://192.168.1.58:10022/search/es/swagger-ui.html#!/search-controller/executeProxyUsingPOST

## request body
## 说明：第一行为graph用来获取数据源，第二行是请求的URI，第三部分是ES的DSL
ccb_dev
POST ccb_dev.company_error/_search
{
    "from" : 0,
    "size" : 200,
    "query": {
        "match_all": {}
    }
}
```

## 接口设计
