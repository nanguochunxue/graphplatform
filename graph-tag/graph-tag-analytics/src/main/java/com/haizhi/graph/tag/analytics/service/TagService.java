package com.haizhi.graph.tag.analytics.service;

import com.haizhi.graph.common.bean.PageResult;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.tag.analytics.model.*;

/**
 * Created by chengmo on 2018/5/3.
 */
public interface TagService {

    Result findByObjectKeys(TagQo params);

    PageResult findPage(TagPageQo params);

    PageResult findPageTagParameters(TagParameterQo params);

    Result findByTagStatus(TagReq params);

    Result updateTagStatus(TagReq params);

    Result applyOrApproveMgt(TagMgtReq params);

    Result get(TagReq params);

    Result insert(TagReq params);

    Result update(TagReq params);

    Result delete(TagReq params);
}
