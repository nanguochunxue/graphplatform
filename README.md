### 构建
mnv clean install

### 模块说明

|模块|类型|端口|用途|
|:-----|:-----|:-----|------------------------------------- |
|graph-common |pom  |无    |公共包 |
|--graph-common-base |jar  |无    |基础包，提供fastjson、guava、apache commons等工具包 |
|--graph-common-core |jar  |无    |核心包，提供JPA相关依赖、公共类 |
|--graph-common-rest |jar  |无    |Restful调用公共包 |
|--graph-common-web |jar  |无    |Web应用公共依赖，提供Swagger的封装，自动配置 |
|--|--|--|--|
|graph-server |pom  |无    |Server层基础接口 |
|--graph-server-api |jar  |无    |通用JAVA接口定义 |
|--graph-server-arango |jar  |无    |Arango操作接口 |
|--graph-server-es |jar  |无    |ES操作接口 |
|--graph-server-hbase |jar  |无    |HBase操作接口 |
|--graph-server-hdfs |jar  |无    |HDFS操作接口 |
|--graph-server-hive |jar  |无    |Hive操作接口 |
|--graph-server-kafka |jar  |无    |kafka操作接口 |
|--graph-server-tiger |jar  |无    |Tiger操作接口 |
|--|--|--|--|
|graph-engine |pom  |无    |引擎层 |
|--graph-engine-base |jar  |无    |基础包，包括表达式规则引擎 |
|--graph-engine-flow |jar  |无    |工作流引擎，包括Spark执行引擎，AirFlow执行引擎（规划中） |
|--|--|--|--|
|graph-sys |pom  |无    |系统模块 |
|--graph-sys-file |jar  |无    |系统文件，统一管理服务 |
|--graph-sys-core |jar  |无    |系统核心包，包括：系统配置等 |
|--graph-sys-auth |jar  |无    |系统权限，用户与角色管理 |
|--|--|--|--|
|graph-dc |pom   |无   |数据中心 |
|--graph-dc-common |jar  |无    |公用包，提供Kafka、HBase的通用类 |
|--graph-dc-core |jar  |无    |核心包，提供元数据CRUD、元数据缓存服务 |
|--graph-dc-arango |web  |10012    |Arango微服务，提供Kafka消费导入Arango |
|--graph-dc-es |web  |10013    |ES微服务，提供Kafka消费导入ES |
|--graph-dc-hbase |web  |10015    |HBase微服务，提供Kafka消费导入HBase、根据RowKey查询 |
|--graph-dc-inbound |jar  |10011    |数据接入后台服务，支持File、HDFS、Hive |
|--graph-dc-inbound-api |web  |10010    |API微服务，数据接入的统一入口，仅支持Restful |
|--graph-dc-store |web  |10017    |数据源管理微服务，提供数据源的创建与连通性测试 |
|--graph-dc-store-api |jar  |10017    |数据源管JAVA接口，提供数据源的CRUD操作 |
|--graph-dc-tiger |web  |10016    |Tiger微服务，提供Kafka消费导入Tiger |
|--|--|--|--|
|graph-search |pom   |无   |搜索服务层 |
|--graph-search-api |jar  |无    |搜索通用JAVA接口定义，用于图查询的多图数据库的适配 |
|--graph-search-arango |web  |10021    |Arango图查询微服务 |
|--graph-search-es |web  |10022    |企业搜索微服务，包括知识卡片搜索 |
|--graph-search-restapi |web  |10020    |搜索微服务，对外搜索统一入口，如：图查询、精确搜索、相关性搜索、查询详情 |
|--graph-search-style |jar  |无    |可视化配置，图查询可视化 |
|--|--|--|--|
|graph-api |web   |10030   |Web应用后台API，统一入口 |
|--|--|--|--|
|graph-plugins |pom   |无   |插件 |
|--graph-dc-inbound-flume |jar  |无    |数据接入插件，包括：流式接入插件、批量接入插件 |
|--graph-dc-inbound-rest |jar  |无    |数据接入API封装 |
|--graph-etl-gp |web  |10041    |GreenPlum数据库ETL微服务插件，提供GP数据的导出 |
|--|--|--|--|
|graph-tag |pom   |无   |标签分析 |
|--graph-tag-analytics |web  |10051    |标签分析，提供标签分布式任务提交YARN执行与结果存储 |
|--graph-tag-core |jar  |无    |提供标签配置的数据库CRUD操作 |
