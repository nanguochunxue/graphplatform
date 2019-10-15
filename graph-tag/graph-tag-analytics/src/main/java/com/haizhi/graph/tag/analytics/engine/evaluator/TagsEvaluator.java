package com.haizhi.graph.tag.analytics.engine.evaluator;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.engine.base.rule.Rule;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TableRow;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.analytics.bean.TagSourceData;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.util.TagUtils;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.DataType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by chengmo on 2018/3/16.
 */
public class TagsEvaluator {

    private static final GLog LOG = LogFactory.getLogger(TagsEvaluator.class);
    public static final String TAG_DOMAIN_KEY = "tag.domain";
    private static final String DEFAULT_TAG_DOMAIN_PATH = "/user/graph/tag/task/TagDomain.json";
    private TagDomain tagDomain;

    public TagsEvaluator(){
        this(DEFAULT_TAG_DOMAIN_PATH);
    }

    public TagsEvaluator(String tagDomainPath) {
        HDFSHelper helper = new HDFSHelper();
        String json = helper.readLine(tagDomainPath);
        this.tagDomain = JSON.parseObject(json, TagDomain.class);
        LOG.info("TagInfo.size={0}", tagDomain.getTagInfoMap().size());
    }

    /**
     * @param key
     * @param value
     * @param task
     * @return
     */
    public List<TagResult> execute(String key, String value, FlowTask task) {
        TagSourceData data = this.getSourceData(key, value, task);
        return this.doExecute(data, tagDomain, task.getTagIds());
    }

    /**
     * @param sourceData
     * @param domain
     * @return
     */
    public List<TagResult> doExecute(TagSourceData sourceData, TagDomain domain, Set<Long> tagIds) {
        // 第一步：获取确定需要解析的标签列表
        List<TagInfo> tagInfoList = domain.getTagInfoList(tagIds);

        // 第二步：逐个标签评估结果
        List<TagResult> resultList = new ArrayList<>();
        for (TagInfo tagInfo : tagInfoList) {
            TagResult tagResult = TagEvaluator.execute(tagInfo, sourceData);
            String value = Objects.toString(tagResult.getValue(), "");
            if (Rule.INVALID_RESULT.equals(value)){
                continue;
            }
            if (tagInfo.getAnalyticsMode() == AnalyticsMode.LOGIC && Rule.BOOLEAN_TRUE.equals(value)){
                resultList.add(tagResult);
            }
        }
        return resultList;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private TagSourceData getSourceData(String key, String value, FlowTask task) {
        TagSourceData data = new TagSourceData();
        data.setObjectKey(key);

        // tables
        String[] tablesData = value.split(FKeys.SEPARATOR_003);
        for (String str : tablesData) {
            String[] tableData = str.split(FKeys.SEPARATOR_002);
            String tableName = tableData[0];
            // hbase
            if (tableName.contains(":")) {
                tableName = StringUtils.substringAfter(tableName, ":");
            }
            // hive
            if (tableName.contains(".")) {
                tableName = StringUtils.substringAfter(tableName, ".");
            }
            String tableRowJson = tableData[1];

            // tableName = tag_value
            if (TagValue._schema.equals(tableName)){
                Map<String, Object> row = JSON.parseObject(tableRowJson, Map.class);

                // DataType = MAP
                String dataType = Getter.get(TagValue.dataType, row);
                if (DataType.MAP.name().equals(dataType.toUpperCase())){
                    String tagValueStr = Getter.get(TagValue.value, row);
                    tagValueStr = tagValueStr.replaceAll("\\(|\\)", "\\$");
                    row.put(TagValue.value, JSON.parseObject(tagValueStr, Map.class));
                }

                String tagId = Getter.get(TagValue.tagId, row);
                // tag_value.tag_id_5
                tableName = TagUtils.getTagReference(tagId);
                TableRow tableRow = new TableRow(tableName, row);
                data.getTableRowMap().put(tableName, tableRow);
                continue;
            }

            // table.row
            Map<String, Object> row = JSON.parseObject(tableRowJson, Map.class);
            TableRow tableRow = new TableRow(tableName, row);
            data.getTableRowMap().put(tableName, tableRow);
        }
        return data;
    }
}
