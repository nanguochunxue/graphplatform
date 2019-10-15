## Redis缓存使用策略
> 1，优先使用SpringBoot的Cache注解方式来做
2，其次使用RedisService接口来做(复杂的不易控制或注解实现不了的）

## RedisKey命名规范

```
com.haizhi.graph.common.redis.key.RKeys

/* DC_DOMIAN                其中dc表示一级模块，
 * com:haizhi:graph:dc      表示区分模块的前缀
 * domain                   表示缓存的实体  
 * dc_graph                 表示缓存的表                
 / 
public static final String DC_DOMIAN = "com:haizhi:graph:dc:domain";
public static final String DC_GRAPH = "com:haizhi:graph:dc:dc_graph";
```

## 1，注解方式

> a0 表示第一个参数 

```
    @Cacheable(cacheNames = RKeys.DC_GRAPH, key = "#a0.graph")
    DcGraphPo save(DcGraphPo dcGraphPo);
    
    @Cacheable(value = RKeys.DC_GRAPH, key = "#a0")
    DcGraphPo findByGraph(String graph)
```

## 2，JAVA接口方式

- `com.haizhi.graph.common.redis.RedisServiceTest.java`
