## 需求&目标

> 1，满足K层展开、全路径、最短路径、社群发现、GraphSQL等方式的复合查询
2，支持复合条件的过滤filter
3，支持结果数据的处理rule

- 一，基础查询
1，关联关系-标准图谱
2，K层展开
3，全路径/最短路径
4，批量查询实体/边
5，聚合

- 二，综合查询
1、多对多全路径（包括一对多)
输入：
A1    B1
A2    B2
A3    B3
转化为全路径
A1-B1，B2，B3
A2-B1，B2，B3
A3-B1，B2，B3

2、批量K层展开
输入：A1，A2，A3
转化为K层展开
A1
A2
A3

3、批量实体路径查询
输入：A1，A2，A3，A4
转化为全路径
A1-A2，A3，A4
A2-A1，A3，A4
A3-A1，A2，A4
A4-A2，A3，A4

### API设计

- API清单
/gdb/api/standardAtlas    关联关系-标准图谱     GdbAtlasQo 
/gdb/api/query            基础查询+综合查询     GdbQo
/gdb/api/queryBySql       GraphSQL查询        GdbSqlQo
/gdb/api/findByIds        根据ID查询实体或边    GdbSchemaQo  


- 输入

/gdb/api/query

GdbQo -> GQuery 

GdbQo
```
{
  "graph": "crm_dev2",
  "type": "K_EXPAND|SHORTEST_PATH|FULL_PATH|PATH"
  "startVertices": [
    "Company/7908891a00d02b29354c4dd5147de439"
  ],
  "endVertices": [],
  "vertexTables": [
    "Company",
    "Person"
  ], 
  "edgeTables": [
    "te_invest",
    "te_officer",
    "te_guarantee",
    "te_transfer",
    "te_concert",
    "te_actual_controller",
    "te_control_shareholder"
  ],
  "direction": "ANY",
  "maxDepth": 1,
  "maxSize": 20000,
  "resultType": "default",
  "rule": {
    "te_officer": {
      "label": "@field(te_officer.position)"
    },
    "te_concert": {
      "label": "一致行动关系"
    },
    "te_actual_controller": {
      "label": "实际控制人"
    },
    "te_control_shareholder": {
      "label": "控股股东"
    },
    "Company": {
      "label": "@field(Company.name)",
      "fields": ["company_code"]
    },
    "Person": {
      "label": "@field(Person.name)"
    }
  },
  "filterIds": [1,6,7], 
  "filter": {
    "logicOperators": ["AND"],
    "rules": [
      {
        "schema": "Company",
        "schemaType": "vertex",
        "field": "reg_city",
        "type": "term",
        "values": ["北京"]
      },
      {
        "logicOperators": ["OR"],
        "rules": [
          {
            "schema": "Company",
            "schemaType": "vertex",
            "field": "reg_city",
            "type": "term",
            "values": ["龙岩"]
          },
          {
            "schema": "te_guarantee",
            "schemaType": "edge",
            "field": "guarantee_amount",
            "type": "range",
            "ranges": [
              {
                "from": 6800000,
                "to": null,
                "include_lower":true,
                "include_upper":true
              }
            ]
          }
        ]
      }
    ]
  }
}
```

- 输出

```
{
  "vertices": [
    {
      "_id": "Company/7908891a00d02b29354c4dd5147de439",
      "_schema":"Company",
      "_label":"海致星图技术（北京）有限公司"
    }
  ],
  "edges": [
    {
      "_id": "te_guarantee/7908891a00d02b29354c4dd5147de439",
      "_schema":"te_guarantee",
      "_from":"Person/7FD19F9C89ED54C03366C6084D14D178",
      "_to":"Company/7908891a00d02b29354c4dd5147de439",
      "_label":"担保"
    }
  ]
}
```