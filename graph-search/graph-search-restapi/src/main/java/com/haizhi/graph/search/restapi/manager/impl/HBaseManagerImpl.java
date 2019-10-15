package com.haizhi.graph.search.restapi.manager.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.constant.HBaseVersion;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.restapi.manager.HBaseManager;
import com.haizhi.graph.sys.core.constant.UrlKeys;
import com.haizhi.graph.sys.core.manager.BaseManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * Created by tanghaiyang on 2019/4/23.
 */
@Service
public class HBaseManagerImpl  extends BaseManager implements HBaseManager {

    private static final GLog LOG = LogFactory.getLogger(HBaseManagerImpl.class);

    private static final String KEY_HBASE = UrlKeys.GRAPH_SEARCH_HBASE;
    private static final String PATH_SEARCH_HBase = "/searchByKeys";

    @Override
    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo, HBaseVersion version) {
        String url = getHBaseUrl(version, PATH_SEARCH_HBase);
        LOG.info("searchByKeys url: {0}", url);
        return doPost(url, searchQo, new ParameterizedTypeReference<Response<KeySearchVo>>(){});
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private String getHBaseUrl(HBaseVersion version, String path) {
        String url;
        switch (version) {
            case V1_3_1:
                url = getUrl(KEY_HBASE, path);
                break;
            default:
                url = StringUtils.EMPTY;
        }
        LOG.info("getGdbUrl:{0}",url);
        return url;
    }

}
