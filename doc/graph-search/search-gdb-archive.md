## 目标
- 1，满足K层展开、全路径、最短路径、社群发现、GraphSQL等方式的复合查询
- 2，支持复合条件的过滤filter
- 3，支持结果数据的处理rule

---
## 查询
### 一、基础查询
- 1，关联关系-标准图谱
- 2，K层展开
  * arango原生的AQL查询方式
  * 多线程遍历方式（借鉴tiger思想）
    * 1、并发请求
        * 同一层同时并发（要确认arangodb集群是否真的支持每次请求一个线程的方式）
        * 超时时间（future.get(timeout, TimeUnit.SECONDS)）
        * 需支持过滤条件（AQL查询单层支持）
        * 不同层需要过滤之前N层的查询结果
            (存在一个问题：
                直接方式，如果要等每层扩展完再扩展后面的层，那就要等到每层最慢的那个线程完毕后才行，查询速度可能比较慢
                自由方式，如果每层每个方向都自由扩展，
                    又可能出现扩展方向慢的方向没有没有剔除已经扩展过的节点，解决方案是使用一个线程共享变量维护一个已经遍历过的顶点。
                    自由扩展还会出现同一个顶点多个线程在执行扩展，导致arangodb的cpu的两个或者多个核重复工作。解决方案是使用显示锁，同一时间不允许请求相同的顶点进行K层展开
            )
            目前考虑直接方案（扩展原有com.haizhi.graph.server.arango.search.ArangoSearchDaoImpl.parseAndQuery方法）
    * 2、线程终止条件
        * 直接方式，每层查完后开始下一层开始时，判断是否到达终点或者达到指定层数（cache缓存之前扩展结果）
        * 自由方式，以K层展开的长度，或者最短路径和全路径的终点是否包含当前顶点为判断依据。

    * 3、结果处理
        * 顶点和边使用Set集合去重

    * 4、通用性
        * 需要考虑做成通用的查询，因为最短路径，全路径可能会用到

- 3，全路径/最短路径
    * arango原生的AQL查询方式
    * 多线程方式(见K层展开)
    * 全路径方案(假定全路径查询必须指定查找层数N，起点集合Source和终点集合End)：
            * a. 先有一个单层的K层展开，输入是顶点集合Source，输出是ResultVertex，
                    另外还有个Result（其实是List<Map>，每个map是展开的起点和终点，之间只有一度关系，并且map的成员只是key（这样可以减小内存）
                    ResultVertex<List<String>> expand(List<String> source, List<Map> Result);
            * b. 迭代N次这个方法，每次迭代都会把Source和Result取并集作为新的Source，每次都会对Result取并集，最后Result就是要的结果集
            * c. 要获取全路径和最短路径都可以通过一种遍历算法进行的，得到一个最短路径或者全路径的subList<Map>
            * d. 最后根据subList<Map>中的key或者id，通过findByIds得到结果，一个包含顶点集合和边集合的json
            * e. 可以考虑将输入集合映射成一个字符串，然后存储缓存，比如本地cache或者redis

- 4，批量查询实体/边
    * 目前支持tiger，arangoDB，neo4j
    * 需要考虑传入limit数量限制和超时时间

- 5，聚合



---
### 二、综合查询
1、多对多全路径（包括一对多)
输入：
A1    B1
A2    B2
A3    B3
转化为全路径
A1-B1，B2，B3
A2-B1，B2，B3
A3-B1，B2，B3

---
### 三、批量K层展开
输入：A1，A2，A3
转化为K层展开
A1
A2
A3

---
### 四、批量实体路径查询
输入：A1，A2，A3，A4
转化为全路径
A1-A2，A3，A4
A2-A1，A3，A4
A3-A1，A2，A4
A4-A2，A3，A4


## API清单
- /gdb/api/standardAtlas    关联关系-标准图谱     GdbAtlasQo
- /gdb/api/query            基础查询+综合查询     GdbQo
    * 使用GdbQo的type字段进行细分查询，如K层展开，聚合查询

- /gdb/api/queryBySql       GraphSQL查询         GdbSqlQo
    * 目前只支持arango，tiger确认不支持，neo4j待定

- /gdb/api/findByIds        根据ID查询实体或边    GdbSchemaQo


---

GdbQuery需要扩展internalOption字段
BaseQo增加属性MultiThreadKExpand


## 实现
多线程K层展开expand
每个线程同时执行K层展开，比如同时3层展开或者1层展开，但实际上，本方案用到的是多线程进行一层展开

 * 输入 GdpQuery，这个bean可以支持带过滤条件，最终转换成带过滤条件的单层展开AQL
 * 输出 List<Map<String,Object>>，每次展开都会有一个顶点的集合和边的集合，这个集合只存入顶点和边的key，不带任何属性
{
    "vertex": [
        111111,
        2222222,
        333333,
        444
    ],
    "edge": [
        99999,
        8888888,
        777777
    ]
}

 * 上面的结果集每个线程的展开都会有，构建一个方法合并上面的结果集，取并集

 * 最后通过findByIds方法，传入上面的结果集，得到最后想要的结果：
{
    "vertex": [
        {
            "_key": 111111,
            "_id": "Company/111111",
            "name": "sdsdsdfs1"
        },
        {
            "_key": 2222222,
            "_id": "Company/2222222",
            "name": "sdsdsdfs2"
        },
        {
            "_key": 333333,
            "_id": "Company/333333",
            "name": "sdsdsdfs3"
        }
    ],
    "edge": [
        {
            "_key": 99999,
            "_id": "Invest/99999",
            "_from": 111111,
            "_to": 2222222,
            "invent": 100
        },
        {
            "_key": 8888888,
            "_id": "Invest/8888888",
            "_from": 333333,
            "_to": 111111,
            "invent": 200
        },
        {
            "_key": 777777,
            "_id": "Invest/777777",
            "_from": 2222222,
            "_to": 333333,
            "invent": 300
        }
    ]
}



