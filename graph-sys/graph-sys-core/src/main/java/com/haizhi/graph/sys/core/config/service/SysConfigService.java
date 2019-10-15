package com.haizhi.graph.sys.core.config.service;

import java.util.Set;

/**
 * Created by chengmo on 2019/4/3.
 */
public interface SysConfigService {

    String getUrl(String key);

    Set<String> findShiroWhiteUrls();

}
