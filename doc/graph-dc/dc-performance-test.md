## DC模块性能测试

### 导入数据到kafka
#### 安装导入工具`import_data_to_kafka`
在目录`import_data_to_kafka`中，执行`python setup.py install`    
执行`import_data_to_kafka --help`可以显示工具的帮助信息    
```shell
Usage: import_data_to_kafka [OPTIONS]

Options:
  --kafka_servers TEXT   example: 192.168.1.17:9092  [required]
  --topic TEXT           example: haizhidev_zy.dc.data.graph_test.sue
                         [required]
  --batch_size INTEGER   每个批次包含的消息数量
  --total_count INTEGER  发送的总消息数
  --data_dir TEXT        example:
                         /opt/performance/arangodb/data/sue.data.arango.json
                         [required]
  --env [cdh|fi]         [required]
  --help                 Show this message and exit.
```

#### fi环境配置
ps: `root账号下尝试`
- 初始化认证

`kinit -V -S "kafka/hadoop.hadoop.com@HADOOP.COM" -k -t /xxx/user.keytab dmp@HADOOP.COM`
- 检查认证
    
`klist`

响应：
```shell
Credentials cache: API:661DF47F-2D7E-4751-9864-94FB6F7501AF
        Principal: dmp@HADOOP.COM

  Issued                Expires               Principal
May 23 14:15:00 2019  May 24 00:15:00 2019  kafka/hadoop.hadoop.com@HADOOP.COM
```

#### 测试数据集
测试数据集: `192.168.1.101:/opt/performance/arangodb/data/sue.data.arango.json`

#### 导入数据

```shell
import_data_to_kafka --kafka_servers 192.168.1.17:9092 --topic=haizhidev_zy.dc.data.graph_test.sue --data_dir=/Users/yang/Downloads/sue.data.100w.json
```

### 性能统计

#### graph-dc-arango
在模块日志文件中`graph-dc-arango.log`中有如下打印
```shell
[2019-05-22 15:01:07]-[INFO]-[com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer.onInboundFinished(AbstractPersistConsumer.java:70)]-{"cudResponse":{"elapsedTime":0,"graph":"graph_test","message":[],"operation":"CREATE_OR_UPDATE","rowsAffected":20,"rowsErrors":0,"rowsIgnored":0,"rowsRead":20,"schema":"sue","success":true},"retryCount":0,"success":true}
[2019-05-22 15:01:07]-[INFO]-[com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer.onInboundFinished(AbstractPersistConsumer.java:70)]-{"cudResponse":{"elapsedTime":0,"graph":"graph_test","message":[],"operation":"CREATE_OR_UPDATE","rowsAffected":20,"rowsErrors":0,"rowsIgnored":0,"rowsRead":20,"schema":"sue","success":true},"retryCount":0,"success":true}
[2019-05-22 15:01:07]-[INFO]-[com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer.onInboundFinished(AbstractPersistConsumer.java:70)]-{"cudResponse":{"elapsedTime":0,"graph":"graph_test","message":[],"operation":"CREATE_OR_UPDATE","rowsAffected":20,"rowsErrors":0,"rowsIgnored":0,"rowsRead":20,"schema":"sue","success":true},"retryCount":0,"success":true}
```

执行以下脚本统计
- 按分钟统计
```shell
cat logs/graph-dc-arango.log |  grep AbstractPersistConsumer.java: | awk -F '\\]-\\[' '{print $1}' | grep -Eo "[0-9]+-[0-9]+-[0-9]+ [0-9]+:[0-9]+" | uniq -c |  awk -F ' ' '{print $1}'
```
执行结果
```shell
1400
7480
4734
4663
1661
```
第一行表示：第一分钟处理了多少个批次的数据
第二行表示: 第二分钟处理了多少个批次的数据
后面的以此类推。
在这个例子中，每个批次的`batch_size`为`100`。
所以每分钟依次导入的数据量为
```
140000
748000
473400
466300
166100
```
某一分钟的插入速度，单位(条/s):
```shell
2334
12467
7890
7772
2769
```



- 按秒统计
cat logs/graph-dc-arango.log |  grep AbstractPersistConsumer.java: | awk -F '\\]-\\[' '{print $1}' | grep -Eo "[0-9]+-[0-9]+-[0-9]+ [0-9]+:[0-9]+:[0-9]]" | uniq -c |  awk -F ' ' '{print $1}'


执行结果
```shell
20
83
182
234
228
232
210
183
28
18
6
101
142
172
49
53
53
45
42
49
25
22
24
24
25
27
22
18
```

第一行表示：第一秒处理了多少个批次的数据
第二行表示: 第二秒处理了多少个批次的数据
后面的以此类推。

在这个例子中，每个批次的`batch_size`为`100`。
所以每秒依次导入的数据量为
```
2000
8300
18200
23400
22800
23200
21000
18300
2800
1800
600
10100
14200
17200
......
4900
2500
2200
2400
2400
2500
2700
2200
1800
```