package com.haizhi.graph.tag.analytics.task.context;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.context.SpringContext;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseContext;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.engine.flow.tools.hive.HiveHelper;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.es.index.mapping.DataType;
import com.haizhi.graph.server.es.index.mapping.TypeMapping;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.analytics.bean.TagValueDaily;
import com.haizhi.graph.tag.analytics.task.scheduler.FlowTaskBuilder;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.core.bean.TagDomain;

import java.io.IOException;

/**
 * Created by chengmo on 2018/4/20.
 */
public class TagHadoopContext {

    public static void initialize(TagContext ctx){
        TagDomain tagDomain = ctx.getTagDomain();
        String graph = tagDomain.getGraphName();

        // upload HDFS
        HDFSHelper helper = new HDFSHelper();
        helper.upsertLine(Constants.PATH_TAG_DOMAIN, JSON.toJSONString(tagDomain));
        helper.close();

        // create hbase table
        createHBaseTables(graph);

        // create hive table
        createHiveTables(graph, ctx.getLocationConfig());

        // create es index
        createEsIndex(graph);
    }

    public static void createHBaseTables(String graph){
        HBaseContext hBaseContext = new HBaseContext();
        StoreURL storeURL = new StoreURL();
        hBaseContext.createDatabaseIfNotExists(storeURL, graph);
        hBaseContext.createTableIfNotExists(storeURL, graph, TagValueDaily._schema);
        hBaseContext.createTableIfNotExists(storeURL, graph, TagValue._schema);
    }

    public static void createHiveTables(String graph, String locationConfig){
        String tagValueDailyScript = FlowTaskBuilder.getTagValueDailyHiveTableScript(graph);
        String tagValueScript = FlowTaskBuilder.getTagValueHiveTableScript(graph);
        HiveHelper hiveHelper = new HiveHelper(locationConfig);
        hiveHelper.createTableIfNotExists(graph, tagValueDailyScript);
        hiveHelper.createTableIfNotExists(graph, tagValueScript);
    }

    public static void createEsIndex(String graph){
        String indexName = graph + Constants.TAG_SUFFIX;
        TypeMapping tagValue = new TypeMapping(indexName, TagValue._schema);
        tagValue.addField(TagValue.tagId, DataType.LONG);
        tagValue.addField(TagValue.objectKey, DataType.KEYWORD);
        tagValue.addField(TagValue.dataType, DataType.KEYWORD);
        tagValue.addField(TagValue.updateTime, DataType.LONG);
        tagValue.addField(TagValue.value, DataType.KEYWORD);

        try {
            String mappingSource = tagValue.builder().string();
            System.out.println(JSON.toJSONString(JSON.parse(mappingSource), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        EsIndexDao esIndexDao = SpringContext.getBean(EsIndexDao.class);
        // TODO
        esIndexDao.createIndex(null, indexName);
        esIndexDao.createType(null, tagValue.getIndexName(), tagValue.getTypeName());
    }
}
