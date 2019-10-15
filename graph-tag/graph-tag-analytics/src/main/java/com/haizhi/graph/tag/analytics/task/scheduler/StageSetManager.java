package com.haizhi.graph.tag.analytics.task.scheduler;

import com.google.common.collect.Maps;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.bean.TagSchemaInfo;
import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.TagDependency;
import com.haizhi.graph.tag.core.domain.TagSchemaType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/4/8.
 */
public class StageSetManager {

    private static GLog LOG = LogFactory.getLogger(StageSetManager.class);

    /**
     * Gets successors by taskIds from tags DAG.
     *
     * @param taskIds
     * @param ctx
     * @return
     */
    public static Set<String> getSuccessors(Set<String> taskIds, TagContext ctx){
        // DAG
        Set<Long> depTagSet = new HashSet<>();
        MutableGraph<Long> graph = GraphBuilder.directed().build();
        for (TagDependency td : ctx.getTagDomain().getDependencies()) {
            graph.putEdge(td.getFromTagId(), td.getToTagId());
            depTagSet.add(td.getFromTagId());
            depTagSet.add(td.getToTagId());
        }

        StageSet stageSet = ctx.getStageSet();
        Set<Long> successors = new HashSet<>();
        for (String taskId : taskIds) {
            Stage.Task task = stageSet.getTask(taskId);
            for (Long tagId : task.getTagIds()) {
                if (!depTagSet.contains(tagId)){
                    continue;
                }
                successors.addAll(graph.successors(tagId));
            }
        }
        if (successors.isEmpty()){
            return Collections.emptySet();
        }

        Set<String> resultSet = new HashSet<>();
        Set<Long> set = new HashSet<>();
        for (Stage.Task task : stageSet.getTasks().values()) {
            if (task.getState() != Stage.TaskState.SCHEDULED){
                continue;
            }
            set.clear();
            set.addAll(successors);
            set.retainAll(task.getTagIds());
            if (!set.isEmpty()){
                resultSet.add(task.getTaskId());
            }
        }
        return resultSet;
    }

    /**
     * @param tagIds
     * @param tagDomain
     * @param domain
     * @return
     */
    public static StageSet get(Set<Long> tagIds, TagDomain tagDomain, Domain domain){
        Map<Long, TagInfo> tagInfoMap = Maps.filterKeys(tagDomain.getTagInfoMap(),
                key -> {return tagIds.contains(key);});
        return get(tagInfoMap, tagDomain, domain);
    }


    /**
     * @param tagDomain
     * @param domain
     * @return
     */
    public static StageSet get(TagDomain tagDomain, Domain domain) {
        Map<Long, TagInfo> tagInfoMap = tagDomain.getTagInfoMap();
        return get(tagInfoMap, tagDomain, domain);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    public static StageSet get(Map<Long, TagInfo> tagInfoMap, TagDomain tagDomain, Domain domain) {
        Map<AnalyticsMode, List<TagInfo>> groupMap = tagInfoMap.values().stream()
                .collect(Collectors.groupingBy(TagInfo::getAnalyticsMode));

        // DAG
        Set<Long> depTagSet = new HashSet<>();
        MutableGraph<Long> graph = GraphBuilder.directed().build();
        for (TagDependency td : tagDomain.getDependencies()) {
            graph.putEdge(td.getFromTagId(), td.getToTagId());
            depTagSet.add(td.getFromTagId());
            depTagSet.add(td.getToTagId());
        }

        // Grouping by the tag schemas and fields
        Map<String, Set<Long>> schemasContent2TagIds = new HashMap<>();
        Map<Long, List<TagSchemaInfo>> tagId2Schemas = tagDomain.getTagSchemaInfoList().stream()
                .collect(Collectors.groupingBy(TagSchemaInfo::getTagId));
        for (Map.Entry<Long, List<TagSchemaInfo>> entry : tagId2Schemas.entrySet()) {
            Long tagId = entry.getKey();
            String contentKey = "";
            for (TagSchemaInfo tsi : entry.getValue()) {
                if (tsi.getType() == TagSchemaType.SQL){
                    contentKey += tsi.getContent();
                    continue;
                }
                contentKey += tsi.getType().name() + tsi.getSchema();
            }
            Set<Long> tagIds = schemasContent2TagIds.get(contentKey);
            if (tagIds == null){
                tagIds = new LinkedHashSet<>();
                schemasContent2TagIds.put(contentKey, tagIds);
            }
            tagIds.add(tagId);
        }

        // Priority
        StageSet stageSet = new StageSet();
        List<Stage.Task> taskList = new ArrayList<>();
        for (Map.Entry<AnalyticsMode, List<TagInfo>> entry : groupMap.entrySet()) {
            switch (entry.getKey()) {
                case STAT:
                    /* 第1优先：统计类(包括定时区间(T+1)统计的汇总) */
                    for (TagInfo tagInfo : entry.getValue()) {
                        long tagId = tagInfo.getTagId();
                        Stage.Task task = new Stage.Task();
                        task.setStageId(1);
                        task.setPriority(11);
                        task.addTagId(tagId);
                        taskList.add(task);
                        if (!depTagSet.contains(tagId)) {
                            continue;
                        }
                        Set<Long> set = graph.predecessors(tagId);
                        if (!set.isEmpty()) {
                            LOG.warn("Ignored the tag[{0}] predecessors[{1}]", tagId, set);
                        }
                    }
                    break;
                case LOGIC:
                    /* 第2优先：不依赖挖掘的逻辑类, 暂不考虑逻辑类标签之间的依赖 */
                    Set<Long> dependDMLogicTags = new LinkedHashSet<>();
                    Map<String, Set<Long>> sameContent2Tags = new HashMap<>();
                    for (TagInfo tagInfo : entry.getValue()){
                        long tagId = tagInfo.getTagId();

                        /* 第4优先：依赖挖掘的逻辑类 */
                        if (depTagSet.contains(tagId)) {
                            Set<Long> set = graph.predecessors(tagId);
                            for (Long depTagId : set) {
                                TagInfo info = tagDomain.getTagInfo(depTagId);
                                if (info.getAnalyticsMode() == AnalyticsMode.DM) {
                                    dependDMLogicTags.add(tagId);
                                    continue;
                                }
                            }
                        }

                        // 第2.1优先：非依赖挖掘的逻辑类 */
                        String contentKey = getSchemasContentKey(tagId, tagId2Schemas);
                        Set<Long> tagIds = sameContent2Tags.get(contentKey);
                        if (tagIds == null){
                            tagIds = new LinkedHashSet<>();
                            sameContent2Tags.put(contentKey, tagIds);
                        }
                        tagIds.add(tagId);
                    }
                    // 4_41
                    for (Long tagId : dependDMLogicTags) {
                        Stage.Task task = new Stage.Task();
                        task.addTagId(tagId);
                        task.setStageId(4);
                        task.setPriority(41);
                        taskList.add(task);
                    }
                    // 2_21
                    for (Set<Long> tagIds : sameContent2Tags.values()) {
                        Stage.Task task = new Stage.Task();
                        task.getTagIds().addAll(tagIds);
                        task.setStageId(2);
                        task.setPriority(21);
                        taskList.add(task);
                    }
                    break;
                case DM:
                    /* 第3优先：挖掘类 */
                    for (TagInfo tagInfo : entry.getValue()) {
                        long tagId = tagInfo.getTagId();
                        Stage.Task task = new Stage.Task();
                        task.addTagId(tagId);
                        /* 第3.1优先：挖掘类原子标签算法 */
                        boolean flag = hasPredecessors(tagId, depTagSet, graph);
                        if (!flag) {
                            task.setPriority(31);
                        }
                        /* 第3.2优先：挖掘类有依赖的标签算法 */
                        else {
                            task.setPriority(32);
                        }
                        task.setStageId(3);
                        taskList.add(task);
                    }
                    break;
            }
        }
        taskList.sort(Comparator.comparingInt(t -> t.getPriority()));
        stageSet.addTasks(taskList);
        return stageSet;
    }

    private static String getSchemasContentKey(long tagId, Map<Long, List<TagSchemaInfo>> tagId2Schemas){
        String contentKey = "";
        List<TagSchemaInfo> list = tagId2Schemas.get(tagId);
        if (list == null){
            return contentKey;
        }
        for (TagSchemaInfo tsi : list) {
            contentKey += tsi.getContent();
        }
        return contentKey;
    }

    private static boolean hasPredecessors(long tagId, Set<Long> depTagSet, MutableGraph<Long> graph) {
        if (!depTagSet.contains(tagId)) {
            return false;
        }
        Set<Long> set = graph.predecessors(tagId);
        return set.isEmpty();
    }
}
