package com.haizhi.graph.tag.analytics.service;

import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.tag.analytics.model.TagValueReq;

/**
 * Created by chengmo on 2018/7/26.
 */
public interface TagValueService {

    Result bulkUpsert(TagValueReq req);
}
