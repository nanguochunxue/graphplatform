## 需求&目标
> 提供统一搜索入口，适配底层的所有搜索服务</br>
如：hbase，es，图查询等所有底层存储库


## 实现思路
> 提供一个统一的查询接口，该接口适应hbase,es,arangodb,tiger等的查询

- 缓存实现：

```
缓存的key是查询条件SearchQo转成的md5字符串，
value是查询条件的的结果CommonVo
下次请求时，先查询缓存，如果已有，就返回缓存结果，
如果不存在缓存就路由到graph-search-arango,graph-search-es等模块执行相应的查询
```

## 接口设计

```
1）通用搜索 - ES、GDB、HBASE
2）图查询 - GDB
3）图谱查询 - GDB
4）主键搜索 - ES、GDB、HBASE
5）原生搜索 - ES、GDB、HBASE
6) 代理搜索 - ES

graph-search-api
|-com.haizhi.graph.search.api
    |-model
        |-qo
            |-SearchQo.java
            |-GdbSearchQo.java
            |-GdbAtlasQo.java
            |-KeySearchQo.java
            |-NativeSearchQo.java
        |-vo
            |-SearchVo.java
            |-GdbSearchVo.java
            |-GdbAtlasVo.java
            |-KeySearchVo.java
            |-NativeSearchVo.java
            |-ProxyVo.java
                |-Map<String, Object> data
    |-service
        |-SearchService.java
            |-Response<SearchVo> search(SearchQo searchQo)
            |-Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo)
            |-Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo)
            |-Response<KeySearchVo> searchByKeys(KeySearchQo searchQo)
            |-Response<NativeSearchVo> searchNative(NativeSearchQo searchQo);
            |-Response<ProxyVo> executeProxy(String request);
    |-gdb

graph-search-restapi
|-com.haizhi.graph.search.restapi
    |-controller
        |-SearchController.java
    |-manager
        |-GdbManager.java
        |-EsManager.java
        |-HBaseManager.java
    |-service
        |-impl
            |-SearchServiceImpl.java

graph-server-api
|-com.haizhi.graph.server.api.es.search
   |-EsSearchDao.java
        |-EsQueryResult searchByDSL(StoreURL storeURL, EsQuery esQuery);
        |-Map<String, Object> executeProxy(StoreURL storeURL, String request);

```




```
{
  "graph": "gap_ccb",
  "schemas": [
    "Company"
  ],
  "keyword": "中国人寿",
  "pageNo": 1,
  "pageSize": 10,
  "option": {
    "log.enabled": true
  },
  "query": [
    {
      "schema": "Company",
      "schemaType": "vertex",
      "field": "name",
      "operator": "MATCH",
      "boost": 3.0
    },
    {
      "schema": "Person",
      "schemaType": "vertex",
      "field": "name",
      "operator": "MATCH",
      "boost": 1.0
    }
  ],
  "filter": {
    "logicOperator": "AND",
    "rules": [
      {
        "schema": "Company",
        "schemaType": "vertex",
        "field": "reg_city",
        "fieldType": "reg_city",
        "operator": "EQ",
        "value": "北京",
        "logicOperator": "AND",
        "rules": []
      },
      {
        "logicOperator": "OR",
        "rules": [
          {
            "schema": "Company",
            "schemaType": "VERTEX",
            "field": "reg_city",
            "fieldType": "STRING",
            "operator": "IN",
            "value": [
              "龙岩"
            ]
          },
          {
            "schema": "te_guarantee",
            "schemaType": "EDGE",
            "field": "guarantee_amount",
            "fieldType": "DOUBLE",
            "operator": "RANGE",
            "value": {
                "from": 6800000,
                "to": null,
                "includeLower": true,
                "includeLower": true
           }
          },
          {
            "schema": "te_guarantee",
            "schemaType": "EDGE",
            "field": "guarantee_amount",
            "fieldType": "DOUBLE",
            "operator": "NOT_RANGE",
            "value": {
                 "from": 6800000,
                 "to": null,
                 "includeLower": true,
                 "includeUpper": true
            }
          }
        ]
      }
    ]
  },
  "aggregation": [
    {
      "key": "amount",
      "field": "amount",
      "type": "term",
      "size": 30
    },
    {
      "key": "amount_stats",
      "field": "amount",
      "type": "term",
      "stats": "stats"
    },
    {
      "key": "amount_min",
      "field": "amount",
      "type": "term",
      "stats": "min"
    },
    {
      "key": "amount_max",
      "field": "amount",
      "type": "term",
      "stats": "max"
    },
    {
      "key": "amount_avg",
      "field": "amount",
      "type": "term",
      "stats": "avg"
    },
    {
      "key": "amount_sum",
      "field": "amount",
      "type": "term",
      "stats": "sum"
    }
  ],
  "sort": [
    {
      "field": "openTime",
      "order": "DESC"
    }
  ]
}

logicOperator
AND,OR

query operator
MATCH EXISTS

filter operator
EQ,NOT_EQ,GT,GTE,LT,LTE,IN,NOT_IN,IS_NULL,IS_NOT_NULL,RANGE,NOT_RANGE,MATCH
```

