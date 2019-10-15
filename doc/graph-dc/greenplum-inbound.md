## 需求&目标
> 需要对GreenPlum数据库进行数据接入
- 1，需要调用GreenPlum卸数的组件生成文件
- 2，需要对卸数的文件接入图平台
- 3，需要有数据接入启动的脚本和进度监控脚本
- 4，需要支持JSON和CSV的数据格式，优先CSV

## 实现思路
- 1，数据源管理==>创建GreenPlum数据源
- 2，数据接入==>创建GreenPlum导入任务
    - 2.1 填写卸数的SQL，需要支持参数占位符
    - 2.2 验证SQL有效性
    - 2.3 提供远程可调用的任务启动脚本
    - 2.4 提供远程可调用的任务进度监控脚本，进度百分比、结果汇总、错误记录的存储位置等
- 3，界面或脚本启动任务
    - 3.1 数据接入微服务调用`卸数微服务(客户端-需研发)`
    - 3.2 卸数微服务调用`卸数组件(行方提供)`生成文件到NAS系统
    - 3.3 卸数组件执行完毕后，通知数据接入微服务从NAS上读取文件导入
- 4，数据接入脚本设计
    - 4.1 通过curl获取任务详情，打印日志
    - 4.2 通过curl启动任务，打印是否成功日志
    - 4.3 监控任务的进度，每3~5秒Curl方式获取任务的执行情况，并打印整体进度百分比
    - 4.4 任务执行完成
        - 4.4.1 打印接入的统计信息，成功数、失败数、耗时等
        - 4.4.2 打印错误记录查看地址，图平台的该任务的错误记录的页面链接

- 卸数服务的调用设计
    - 1，graph-dc-inbound模块调用
        - 1.1 调用graph-etl-gp.startExport导出API
        - 1.2 获取Redis结果，需要设置超时时间，循环请求Redis existsKey() get()
    - 2，graph-etl-gp模块的startExport实现
        - 1.1 启动异步任务成功后返回Response
        - 1.2 异步调用数组件Perl脚本
        - 1.3 异步调用成功后插入执行结果到Redis
            ```
            RedisKey的定义：DC_TASK_GRAPH_ETL_GP = "com:haizhi:graph:dc:task:graph_etl_gp";
            Key = DC_TASK_GRAPH_ETL_GP + ":" + ${taskInstanceId}
            Response
                success
                message
                payload
                    exitCode
                    outputFile   
            ```            
    

## 接口设计

```
graph-dc-inbound
graph-server-api
    |-com.haizhi.graph.server.api
        |-gp
            |-GreenPlumDao.java
graph-server-gp        
    |-com.haizhi.graph.server.gp
        |-GreenPlumDaoImpl.java
graph-plugins
    |-graph-etl-gp
        |-com.haizhi.graph.plugins.etl.gp
            |-controller
                |-GreenPlumController.java
                    startExport()
                    exportProgress()
            |-service
                |-impl
                    |-GreenPlumService.java
                |-GreenPlumService.java

```

## 参考

### 卸数脚本Perl

- 输入
```
标建议的为建议输入参数，其它参数根据实际内容填写

组件名：export_data.pl
参数1: -db_access_id  
数据库访问控制ID
参数2: -outfile  
导出文件(全路径)
参数3: -table_name  
卸数目标表
参数4: -table_filt
过滤条件(不带WHERE关键字)
参数5: -fixed  
定长标志(Y-定长 N-非定长-建议)
参数6: -delim 
分隔符
参数7: -enddelim  
是否含末尾分隔符(Y-是 N-否)
参数8: -charset  
卸数字符集(如：utf8/gbk，建议utf8)
参数9: -ctl_file_type  
控制文件类型(0/1/2/3)，结果是否生成控制文件和DDL文件
0-	不需要-建议 
1-	DDL文件和控制文件
2-	DDL文件
3-	控制文件
参数15: -select_list 
[选填]按字段卸载(不区分大小写)，需要卸载的字段列表
逗号分隔，不能与nonselect_list同时使用

```

- 输出

```
成功返回0，失败返回1
```