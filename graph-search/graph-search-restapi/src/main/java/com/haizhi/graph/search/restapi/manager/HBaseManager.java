package com.haizhi.graph.search.restapi.manager;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.constant.HBaseVersion;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;

/**
 * Created by chengmo on 2019/4/23.
 */
public interface HBaseManager {

    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo, HBaseVersion version);
}
