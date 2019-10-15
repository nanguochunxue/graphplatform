package com.haizhi.graph.search.restapi.manager.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.constant.EsVersion;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.qo.SearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.api.model.vo.SearchVo;
import com.haizhi.graph.search.restapi.manager.EsManager;
import com.haizhi.graph.sys.core.constant.UrlKeys;
import com.haizhi.graph.sys.core.manager.BaseManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * Created by chengmo on 2019/4/23.
 */
@Service
public class EsManagerImpl extends BaseManager implements EsManager {

    private static final GLog LOG = LogFactory.getLogger(EsManagerImpl.class);

    private static final String KEY_ES = UrlKeys.GRAPH_SEARCH_ES;
    private static final String PATH_SEARCH = "/search";
    private static final String PATH_SEARCH_BY_KEYS = "/searchByKeys";
    private static final String PATH_SEARCH_NATIVE = "/searchNative";
    private static final String PATH_EXECUTE_PROXY = "/executeProxy";

    @Override
    public Response<SearchVo> search(SearchQo searchQo, EsVersion version) {
        return doPost(getEsUrl(version, PATH_SEARCH), searchQo,
                new ParameterizedTypeReference<Response<SearchVo>>() {
                });
    }

    @Override
    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo, EsVersion version) {
        return doPost(getEsUrl(version, PATH_SEARCH_BY_KEYS), searchQo,
                new ParameterizedTypeReference<Response<KeySearchVo>>() {
                });
    }

    @Override
    public Response<NativeSearchVo> searchNative(NativeSearchQo searchQo, EsVersion version) {
        return doPost(getEsUrl(version, PATH_SEARCH_NATIVE), searchQo,
                new ParameterizedTypeReference<Response<NativeSearchVo>>() {
                });
    }

    @Override
    public Response<Object> executeProxy(String request, EsVersion version) {
        return doPost(getEsUrl(version, PATH_EXECUTE_PROXY), request,
                new ParameterizedTypeReference<Response<Object>>() {
                });
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private String getEsUrl(EsVersion version, String path) {
        String url;
        switch (version) {
            case V5_4_3:
                url = getUrl(KEY_ES, path);
                break;
            case V6_1_5:
                url = getUrl(KEY_ES, path);
                break;
            default:
                url = StringUtils.EMPTY;
        }
        LOG.info("getGdbUrl={0}, KEY_ES={1}", url, KEY_ES);
        return url;
    }

}
