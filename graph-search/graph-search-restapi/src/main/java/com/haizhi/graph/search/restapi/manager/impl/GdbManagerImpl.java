package com.haizhi.graph.search.restapi.manager.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.constant.GdbVersion;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;
import com.haizhi.graph.search.restapi.manager.GdbManager;
import com.haizhi.graph.sys.core.constant.UrlKeys;
import com.haizhi.graph.sys.core.manager.BaseManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * Created by chengmo on 2019/4/23.
 */
@Service
public class GdbManagerImpl extends BaseManager implements GdbManager {

    private static final GLog LOG = LogFactory.getLogger(GdbManagerImpl.class);

    private static final String KEY_ARANGO = UrlKeys.GRAPH_SEARCH_ARANGO;
    private static final String KEY_TIGER = UrlKeys.GRAPH_SEARCH_TIGER;
    private static final String PATH_SEARCH_ATLAS = "/searchAtlas";
    private static final String PATH_SEARCH_GDB = "/searchGdb";
    private static final String PATH_SEARCH_BY_KEYS = "/searchByKeys";
    private static final String PATH_SEARCH_NATIVE = "/searchNative";

    @Override
    public Response<SearchVo> search(SearchQo searchQo, GdbVersion version) {
        return null;
    }

    @Override
    public Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo, GdbVersion version) {
        return doPost(getGdbUrl(version, PATH_SEARCH_GDB), searchQo,
                new ParameterizedTypeReference<Response<GdbSearchVo>>() {
                });
    }

    @Override
    public Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo, GdbVersion version) {
        return doPost(getGdbUrl(version, PATH_SEARCH_ATLAS), searchQo,
                new ParameterizedTypeReference<Response<GdbAtlasVo>>() {
                });
    }

    @Override
    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo, GdbVersion version) {
        return doPost(getGdbUrl(version, PATH_SEARCH_BY_KEYS), searchQo,
                new ParameterizedTypeReference<Response<KeySearchVo>>() {
                });
    }

    @Override
    public Response<NativeSearchVo> searchNative(NativeSearchQo searchQo, GdbVersion version) {
        return doPost(getGdbUrl(version, PATH_SEARCH_NATIVE), searchQo,
                new ParameterizedTypeReference<Response<NativeSearchVo>>() {
                });
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private String getGdbUrl(GdbVersion version, String path) {
        String url;
        switch (version) {
            case ATLAS:
                url = getUrl(KEY_ARANGO, path);
                break;
            case TIGER:
                url = getUrl(KEY_TIGER, path);
                break;
            default:
                url = StringUtils.EMPTY;
        }
        LOG.info("getGdbUrl:{0}",url);
        return url;
    }
}
