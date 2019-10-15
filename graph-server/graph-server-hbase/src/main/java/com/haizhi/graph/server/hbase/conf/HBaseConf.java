package com.haizhi.graph.server.hbase.conf;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.context.SpringContext;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.EnvVersion;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2017/10/24.
 */
public class HBaseConf {
    private static final GLog LOGGER = LogFactory.getLogger(HBaseConf.class);

    public static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
    public static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
    public static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop";

    private static boolean securityEnabled;
    private static String userPrincipal;

    static {
        try {
            securityEnabled = SpringContext.getProperty(Constants.HADOOP_SECURITY_ENABLED, Boolean.class);
            userPrincipal = SpringContext.getProperty(Constants.HADOOP_SECURITY_USERPRINCIPAL);
        } catch (NullPointerException ignored) {
            // ignored
        }
        LOGGER.info("securityEnabled={0}, userPrincipal={1}", securityEnabled, userPrincipal);
    }

    public static Configuration create() {
        if (securityEnabled) {
            // init
            Configuration conf = HBaseConfiguration.create();
            conf.addResource(new Path(Resource.getPath("core-site.xml")));
            conf.addResource(new Path(Resource.getPath("hdfs-site.xml")));
            conf.addResource(new Path(Resource.getPath("hbase-site.xml")));

            //login
            if (User.isHBaseSecurityEnabled(conf)) {
                String userKeytabFile = Resource.getPath("user.keytab");
                String krb5File = Resource.getPath("krb5.conf");

                try {
                    LoginUtil.setJaasConf(ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, userPrincipal, userKeytabFile);
                    LoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY,
                            ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
                    LoginUtil.login(userPrincipal, userKeytabFile, krb5File, conf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return conf;
        }

        //CDH
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(new Path(Resource.getPath("hbase-site-cdh.xml")));
        return conf;
    }

    public static Configuration create(StoreURL storeURL) {
        Configuration conf = HBaseConfiguration.create();
        boolean securityEnabled = storeURL.isSecurityEnabled();
        String userPrincipal = storeURL.getUserPrincipal();
        Map<String, String> filePath = storeURL.getFilePath();
        addResources(filePath, conf);
        if (!securityEnabled && StringUtils.isNotEmpty(userPrincipal)) {
            System.setProperty("HADOOP_USER_NAME", userPrincipal);
            return conf;
        }
        String envVersion = storeURL.getEnvVersion();
        envVersion = Objects.isNull(envVersion) ? "" : envVersion;
        if (envVersion.toUpperCase().startsWith(EnvVersion.FI.name())) {
            // init
            //conf.addResource(new Path(filePath.get("core-site.xml")));
            //conf.addResource(new Path(filePath.get("hdfs-site.xml")));
            //conf.addResource(new Path(filePath.get("hbase-site.xml")));

            //login
            if (User.isHBaseSecurityEnabled(conf)) {
                String userKeytabFile = filePath.get("user.keytab");
                String krb5File = filePath.get("krb5.conf");

                try {
                    LoginUtil.setJaasConf(ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, userPrincipal, userKeytabFile);
                    LoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY,
                            ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
                    LoginUtil.login(userPrincipal, userKeytabFile, krb5File, conf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return conf;
        }
        // ksyun for hbase2.x
        if (envVersion.startsWith(EnvVersion.KSYUN.name())) {
            try {
                //conf.addResource(new Path(filePath.get("hbase-site.xml")));
                System.setProperty("java.security.krb5.conf", filePath.get("krb5.conf"));
                System.setProperty("sun.security.krb5.debug", "false");
                conf.set("hadoop.security.authentication", "kerberos");
                UserGroupInformation.setConfiguration(conf);
                UserGroupInformation ugi = UserGroupInformation
                        .loginUserFromKeytabAndReturnUGI(userPrincipal, filePath.get("user.keytab"));
                UserGroupInformation.setLoginUser(ugi);
                //HBaseAdmin.available(conf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return conf;
    }

    public static void addResources(Map<String, String> filePath, Configuration conf) {
        if (MapUtils.isEmpty(filePath)) {
            return;
        }
        for (Map.Entry<String, String> entry : filePath.entrySet()) {
            String resourceName = entry.getKey();
            if (resourceName.toLowerCase().endsWith("-site.xml")) {
                conf.addResource(new Path(filePath.get(resourceName)));
            }
        }
    }

}