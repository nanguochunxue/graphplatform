package com.haizhi.graph.dc.core.service.impl;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haizhi.graph.common.cache.CacheFactory;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.core.service.DcMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2018/10/23.
 */
@Service
public class DcMetadataCacheImpl implements DcMetadataCache {

    private static final GLog LOG = LogFactory.getLogger(DcMetadataCacheImpl.class);

    private static final Domain EMPTY = new Domain();

    @Autowired
    private DcMetadataService dcMetadataService;

    private LoadingCache<String, Domain> domainCache;

    public DcMetadataCacheImpl() {
        this.domainCache = CacheFactory.create(new CacheLoader<String, Domain>() {
            @Override
            public Domain load(String key) throws Exception {
                return getDomainByGraph(key);
            }
        }, 30);
    }

    @Override
    public Domain getDomain(String graph) {
        try {
            return domainCache.get(graph);
        } catch (ExecutionException e) {
            LOG.error(e);
        }
        return EMPTY;
    }

    @Override
    public void refresh(String key) throws Exception {
        try {
            domainCache.invalidate(key);
            LOG.info("Success to refresh metaDataCache by key={0}", key);
        } catch (Exception e) {
            throw e;
        }
    }

    ///////////////////////
    // protected functions
    ///////////////////////
    protected Domain getDomainByGraph(String graph) {
        try {
            return dcMetadataService.getDomain(graph);
        } catch (Exception e) {
            LOG.error(e);
        }
        return EMPTY;
    }
}
