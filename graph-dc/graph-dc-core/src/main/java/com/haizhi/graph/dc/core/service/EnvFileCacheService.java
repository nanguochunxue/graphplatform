package com.haizhi.graph.dc.core.service;

import java.util.Map;

/**
 * Created by chengangxiong on 2019/03/30
 */
public interface EnvFileCacheService {

    Map<String, String> findEnvFile(Long envId);

    void deleteEnvFile(Long envId, String fileName);
}
