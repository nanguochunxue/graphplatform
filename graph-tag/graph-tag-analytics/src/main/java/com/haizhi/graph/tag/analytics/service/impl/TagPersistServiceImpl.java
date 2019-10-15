package com.haizhi.graph.tag.analytics.service.impl;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.v0.Response;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.tag.analytics.service.TagCategoryService;
import com.haizhi.graph.tag.analytics.service.TagPersistService;
import com.haizhi.graph.tag.analytics.service.builder.TagCatNamesBuilder;
import com.haizhi.graph.tag.core.dao.TagDAO;
import com.haizhi.graph.tag.core.domain.Tag;
import com.haizhi.graph.tag.core.domain.TagCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/8/8.
 */
@Service
public class TagPersistServiceImpl implements TagPersistService {
    private static final GLog LOG = LogFactory.getLogger(TagPersistService.class);
    private static final String TAG = "tag";
    private static final String TAG_CATEGORY_NAME = "tag_category_name";

    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private TagCategoryService tagCategoryService;
    @Autowired
    private EsIndexDao esIndexDao;

    @PostConstruct
    public void loadAndPersistData(){
        List<String> graphList;
        try {
            graphList = tagDAO.findGraphNames();
        } catch (Exception e) {
            LOG.error(e);
            return;
        }
        for (String graph : graphList) {
            // TODO
            if (!esIndexDao.createIndex(null, graph)){
                break;
            }
            if (!esIndexDao.createType(null, graph, TAG)){
                break;
            }
            syncDataToEs(graph);
        }
    }

    @Override
    public Response syncDataToEs(String graph) {
        Response response = Response.success();
        try {
            List<Tag> tagList = tagDAO.findAllTags(graph);
            if (tagList.isEmpty()){
                return response;
            }

            // sources
            List<Source> sourceList = new ArrayList<>();
            Map<Long, TagCategory> catMap = tagCategoryService.getCategoryMap(graph);
            for (Tag tag : tagList) {
                Source source = new Source(String.valueOf(tag.getId()));
                source.setSource(JSONUtils.toMap(tag));
                List<TagCategory> catList = tagCategoryService.getParentList(tag.getTagCategoryId(), catMap);
                String tagCategoryName = TagCatNamesBuilder.get(catList);
                source.addField(TAG_CATEGORY_NAME, tagCategoryName);
                sourceList.add(source);
            }
            boolean success = esIndexDao.bulkUpsert(null, graph, TAG, sourceList).isSuccess();
            response.setSuccess(success);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            LOG.error(e);
        }
        return response;
    }

    @Override
    public boolean insertOrUpdate(String graph, Long tagId) {
        boolean success;
        try {
            Tag tag = tagDAO.findOne(tagId);
            if (Objects.isNull(tag)){
                return false;
            }
            Long catId = tag.getTagCategoryId();
            List<TagCategory> catList = tagCategoryService.getParentList(graph, catId);
            String tagCategoryName = TagCatNamesBuilder.get(catList);
            List<Source> sourceList = new ArrayList<>();
            Source source = new Source(String.valueOf(tagId));
            source.setSource(JSONUtils.toMap(tag));
            source.addField(TAG_CATEGORY_NAME, tagCategoryName);
            sourceList.add(source);
            success = esIndexDao.bulkUpsert(null, graph, TAG, sourceList).isSuccess();
        } catch (Exception e) {
            success = false;
            LOG.error(e);
        }
        return success;
    }

    ///////////////////////
    // private functions
    ///////////////////////
}
