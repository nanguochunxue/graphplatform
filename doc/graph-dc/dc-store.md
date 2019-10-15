## 需求&目标

> 实现数据源和环境管理，包括：hbase、es、gdb、hive、hdfs

## 实现思路

- 前端提示
    - 1，HBase
        - 1.1 输出框提示：hbase.zookeeper.quorum:clientPort
        - 1.2 示例：192.168.1.111,192.168.1.112,192.168.1.113:24002
    - 2，ES-5.4.3
        - 1.1 输出框提示：host1:tcp.port1,host1:tcp.port2,host1:tcp.port3
        - 1.2 示例：192.168.1.21:9300,192.168.1.22:9300,192.168.1.23:9300
    - 3，ES-6.x-fic80
        - 1.1 输出框提示：host1:http.port1,host2:http.port2,host3:http.port3
        - 1.2 示例：192.168.1.111:24148,192.168.1.112:24148,192.168.1.113:24148    
    - 4，GDB
        - 1.1 输出框提示：host:port
        - 1.2 示例：192.168.1.111:8529 
    - 5，GreenPlum
        - 1.1 输出框提示：jdbc:postgresql://host:port/dbName
        - 1.2 示例：jdbc:postgresql://192.168.1.11:5432/db_test        
        - 1.3 卸数服务输出框提示：http://host:port/plugins/etl/gp/
        - 1.4 卸数服务示例：http://192.168.1.213:10041/plugins/etl/gp/
    - 6，ive-无安全
        - 1.1 输出框提示：jdbc:hive2://host:port/dbName
        - 1.2 示例：jdbc:hive2://192.168.1.21:10000/default
    - 7，Hive-有安全认证的
        - 1.1 输出框提示：jdbc:hive2://host:port,host:port,host:port/;principal=...
        - 1.2 示例：jdbc:hive2://192.168.1.111:24002,192.168.1.112:24002,192.168.1.113:24002/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;sasl.qop=auth-conf;auth=KERBEROS;principal=hive/hadoop.hadoop.com@HADOOP.COM;    
    - 8，HDFS
        - 1.1 输出框提示：hdfs://host:ip
        - 1.2 示例：hdfs://192.168.1.21:8020        

## 接口设计