package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.cache.Cacheable;
import com.haizhi.graph.dc.core.bean.Domain;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcMetadataCache extends Cacheable{

    Domain getDomain(String graph);
}
