package com.haizhi.graph.dc.store.api.service.impl;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haizhi.graph.common.cache.CacheFactory;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import com.haizhi.graph.dc.core.model.vo.DcStoreVo;
import com.haizhi.graph.dc.core.service.DcEnvService;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.core.service.EnvFileCacheService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.EnvVersion;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import com.haizhi.graph.sys.file.service.SysDictService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by chengangxiong on 2019/04/10
 */
@Service
public class StoreUsageServiceImpl implements StoreUsageService {

    private static final GLog LOG = LogFactory.getLogger(StoreUsageServiceImpl.class);
    private static final StoreURL _NULL_ = new StoreURL();

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private DcEnvService dcEnvService;

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private EnvFileCacheService envFileCacheService;

    private LoadingCache<String, StoreURL> storeURLCache;

    public StoreUsageServiceImpl() {
        this.storeURLCache = CacheFactory.create(new CacheLoader<String, StoreURL>() {
            @Override
            public StoreURL load(String key) throws Exception {
                return doFindStoreURL(key);
            }
        }, 30);
    }

    @Override
    public StoreURL findStoreURL(@NonNull String graph, @NonNull StoreType storeType) {
        try {
            String key = buildStoreCacheKey(graph, storeType);
            return storeURLCache.get(key);
        } catch (ExecutionException e) {
            LOG.error(e);
        }
        return _NULL_;
    }

    @Override
    public void refresh(String key) throws Exception {
        try {
            storeURLCache.invalidate(key);
            LOG.info("Success to refresh storeCache by key={0}", key);
        } catch (Exception e) {
            throw e;
        }
    }

    public StoreURL findStoreURL(@NonNull Long envId) {
        StoreURL storeURL = new StoreURL();
        fillStoreUrl(storeURL, envId);
        return storeURL;
    }

    private StoreURL doFindStoreURL(String key) {
        String[] arr = key.split("#");
        StoreType storeType = StoreType.fromCode(arr[1]);
        return doFindStoreURL(arr[0], storeType);
    }

    private StoreURL doFindStoreURL(String graph, StoreType storeType) {
        Domain domain = dcMetadataCache.getDomain(graph);
        if (domain.invalid()) {
            throw new RuntimeException("no config [" + graph + "] found");
        }
        DcStoreVo store = domain.getStoreMap().get(storeType);
        if (Objects.isNull(store)) {
            throw new RuntimeException("store in graph [" + graph + "] and storeType [" + storeType + "] not exists");
        }
        StoreURL storeURL = new StoreURL(store.getUrl(), store.getUser(), store.getPassword());
        storeURL.setStoreVersion(store.getVersion());
        Long envId = store.getEnvId();
        if (Objects.nonNull(envId)) {
            fillStoreUrl(storeURL, envId);
        }
        return storeURL;
    }

    private void fillStoreUrl(StoreURL storeURL, Long envId) {
        DcEnvPo dcEnv = dcEnvService.findOne(envId);
        if (Objects.nonNull(dcEnv)) {
            SysDictPo dictPo = sysDictService.findById(dcEnv.getVersionDictId());
            storeURL.setEnvVersion(Objects.nonNull(dictPo) ? dictPo.getValue() : EnvVersion.CDH.name());
            Map<String, String> fileMap = envFileCacheService.findEnvFile(envId);
            storeURL.setFilePath(fileMap);
            storeURL.setSecurityEnabled(dcEnv.getSecurityEnabled().equals(Constants.Y));
            storeURL.setUserPrincipal(dcEnv.getUser());
        }
    }

    private String buildStoreCacheKey(String graph, StoreType storeType) {
        return graph + "#" + storeType.getName();
    }
}