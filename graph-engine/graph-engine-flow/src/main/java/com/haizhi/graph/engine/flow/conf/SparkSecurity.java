package com.haizhi.graph.engine.flow.conf;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.hbase.conf.LoginUtil;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

/**
 * Created by chengmo on 2018/9/29.
 */
public class SparkSecurity {
    private static final GLog LOG = LogFactory.getLogger(SparkSecurity.class);

    public static void login(String userPrincipal, String userConfPath){
        try {
            LOG.info("Spark security prepare start.");
            System.setProperty("HADOOP_USER_NAME", userPrincipal);
            System.setProperty("user.name", userPrincipal);
            String userKeytabPath = userConfPath + "/user.keytab";
            String krb5ConfPath = userConfPath + "/krb5.conf";
            // kafka
            LoginUtil.setKrb5Config(krb5ConfPath);
            LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
            LoginUtil.setJaasFile(userPrincipal, userKeytabPath);
            // spark + hbase
            Configuration conf = new Configuration();
            LoginUtil.login(userPrincipal, userKeytabPath, krb5ConfPath, conf);
            LOG.info("Spark security prepare success.");
        } catch (IOException e) {
            LOG.error("Spark security prepare failure.", e);
        }
    }
}
