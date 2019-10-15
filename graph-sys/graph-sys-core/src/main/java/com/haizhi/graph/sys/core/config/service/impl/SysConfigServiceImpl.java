package com.haizhi.graph.sys.core.config.service.impl;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haizhi.graph.common.cache.CacheFactory;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.core.config.dao.SysConfigDao;
import com.haizhi.graph.sys.core.config.model.SysConfigPo;
import com.haizhi.graph.sys.core.config.service.SysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2019/4/3.
 */
@Repository
public class SysConfigServiceImpl implements SysConfigService {

    private static final GLog LOG = LogFactory.getLogger(SysConfigServiceImpl.class);
    private static final String TYPE = "DMP";
    private static final String SUB_TYPE_URL = "URL";
    private static final String SHIRO_WHITE_URL_VALUE = "anon";

    @Autowired
    private SysConfigDao sysConfigDao;


    private LoadingCache<String, Map<String, String>> configCache;

    public SysConfigServiceImpl(){
        this.configCache = CacheFactory.create(new CacheLoader<String, Map<String, String>>() {
            @Override
            public Map<String, String> load(String key) throws Exception {
                return findSysConfigs(key);
            }
        });
    }

    @Override
    public String getUrl(String key) {
        return getSysConfig(SUB_TYPE_URL,key);
    }

    @Override
    public Set<String> findShiroWhiteUrls() {
        List<SysConfigPo> sysConfigPos = sysConfigDao.findByValue(SHIRO_WHITE_URL_VALUE);
        if (CollectionUtils.isEmpty(sysConfigPos)){
            return Collections.emptySet();
        }
        return sysConfigPos.stream().map(SysConfigPo::getKey).collect(Collectors.toSet());
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private String getSysConfig(String subType, String key){
        try {
            Map<String, String> map = configCache.get(TYPE);
            if (Objects.isNull(map)){
                return StringUtils.EMPTY;
            }
            return map.getOrDefault(buildCacheKey(subType,key), "");
        } catch (ExecutionException e) {
            LOG.error(e);
        }
        return StringUtils.EMPTY;
    }

    private Map<String, String> findSysConfigs(String type) {
        try {
            // TODO: redis cache
            List<SysConfigPo> list = sysConfigDao.findByType(type);
            return list.stream().collect(Collectors.toMap(e -> buildCacheKey(e.getSubType(),e.getKey()),
                    e -> e.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    private String buildCacheKey(String subType,String key){
        String cacheKey = String.format("%s-%s",subType,key);
        return cacheKey;
    }

}
