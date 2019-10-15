package com.haizhi.graph.common.context;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.MXBeanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengmo on 2018/9/27.
 */
public class Module {
    private static final GLog LOG = LogFactory.getLogger(Module.class);
    private final static LocalHost LOCALHOST = new LocalHost();
    public static final String KEY_SEPARATOR = "#";
    public static String moduleName;
    public static String host;
    public static Integer pid;

    static {
        try {
            String path = Module.class.getResource("/").getPath();
            if (path.contains("/target/")){
                moduleName = StringUtils.substringBeforeLast(path, "/target/");
                moduleName = StringUtils.substringAfterLast(moduleName, "/");
            } else {
                moduleName = StringUtils.substringBeforeLast(path, "/lib/");
                moduleName = StringUtils.substringAfterLast(moduleName, "/");
                moduleName = StringUtils.substringBefore(moduleName, ".");
                moduleName = StringUtils.substringBeforeLast(moduleName, "-");
            }
            host = LOCALHOST.getIp();
            pid = MXBeanUtils.getRuntimePID();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public static String getName(){
        return moduleName;
    }

    public static String getFullName() {
        return String.join(KEY_SEPARATOR, moduleName, host, String.valueOf(pid));
    }
}
