package com.haizhi.graph.tag.analytics.service;

import com.haizhi.graph.common.model.v0.Response;

/**
 * Created by chengmo on 2018/8/8.
 */
public interface TagPersistService {

    Response syncDataToEs(String graph);

    boolean insertOrUpdate(String graph, Long tagId);
}
