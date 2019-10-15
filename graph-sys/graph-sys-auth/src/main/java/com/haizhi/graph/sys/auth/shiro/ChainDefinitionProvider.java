package com.haizhi.graph.sys.auth.shiro;

import com.haizhi.graph.sys.core.config.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/1/4.
 */
public class ChainDefinitionProvider {

    @Autowired
    private SysConfigService sysConfigService;

    @Transactional
    public Map<String, String> getAllRolesPermissions() {
        Map<String, String> map = new LinkedHashMap<>();
        Set<String> whiteUrls = sysConfigService.findShiroWhiteUrls();
        whiteUrls.forEach(url -> map.put(url, "anon"));
        map.put("/**", "user");
        return map;
    }
}
