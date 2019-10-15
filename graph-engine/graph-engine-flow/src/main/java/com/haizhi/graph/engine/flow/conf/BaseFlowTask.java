package com.haizhi.graph.engine.flow.conf;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2019/2/11.
 */
@Data
public class BaseFlowTask {
    private static final String DEFAULT_MASTER = "local[*]";
    /** debug */
    private boolean debugEnabled;
    private boolean debugLauncher;
    /** run mode */
    private boolean runLocallyEnabled;
    private String master = DEFAULT_MASTER;
    /** security */
    private boolean securityEnabled;
    private String userPrincipal;
    private String userConfPath;
    /** conf */
    private String hadoopConfDir;//local
    private Map<String, String> properties = new HashMap<>();

    public void putProperty(String key, String value) {
        this.properties.put(key, value);
    }
}
