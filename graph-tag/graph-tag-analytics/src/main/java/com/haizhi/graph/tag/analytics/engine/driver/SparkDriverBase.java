package com.haizhi.graph.tag.analytics.engine.driver;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.engine.flow.action.LauncherDriver;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.conf.SparkSecurity;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by chengmo on 2018/4/13.
 */
public class SparkDriverBase extends LauncherDriver {

    @Override
    protected void run(String[] args) throws Exception {
    }

    protected FlowTask getFlowTask(String[] args) {
        if (args.length < 1) {
            System.err.println(Arrays.asList(args));
            throw new IllegalArgumentException("args.length < 1");
        }
        System.out.println("args=" + Arrays.asList(args));
        String appArgs = args[0];
        System.out.println(appArgs);
        this.securityLogin(appArgs);

        // task
        HDFSHelper helper = new HDFSHelper();
        String path = StringUtils.substringBefore(appArgs, FKeys.SEPARATOR_002);
        FlowTask task = JSON.parseObject(helper.readLine(path), FlowTask.class);
        this.debug = task.isDebugEnabled();
        this.security = task.isSecurityEnabled();
        System.out.println(JSON.toJSONString(task, true));
        return task;
    }

    private void securityLogin(String appArgs){
        if (!appArgs.contains(FKeys.SEPARATOR_002)){
            return;
        }
        String str = StringUtils.substringAfter(appArgs, FKeys.SEPARATOR_002);
        String userPrincipal = StringUtils.substringBefore(str, FKeys.SEPARATOR_001);
        String userConf = StringUtils.substringAfter(str, FKeys.SEPARATOR_001);
        SparkSecurity.login(userPrincipal, userConf);
    }
}
