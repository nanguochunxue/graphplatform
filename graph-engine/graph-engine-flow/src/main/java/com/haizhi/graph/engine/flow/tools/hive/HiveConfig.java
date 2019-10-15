package com.haizhi.graph.engine.flow.tools.hive;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.util.PropertiesUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Properties;

/**
 * Created by chengmo on 2018/4/16.
 */
public class HiveConfig {

    private static final String HIVE_URL = "hive.url";
    private static final String HIVE_USERNAME = "hive.username";
    private static final String HIVE_PASSWORD = "hive.password";
    private static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
    private static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop.hadoop.com";

    private String url;
    private String userName;
    private String password;

    public HiveConfig(String locationConfig){
        Properties props = PropertiesUtils.load("/" + locationConfig);
        if (!props.containsKey(HIVE_URL)){
            throw new IllegalArgumentException("hive url must not be empty.");
        }
        this.url = props.getProperty(HIVE_URL);
        this.userName = props.getProperty(HIVE_USERNAME);
        this.password = props.getProperty(HIVE_PASSWORD);
        boolean security = BooleanUtils.toBoolean(props.getProperty(Constants.HADOOP_SECURITY_ENABLED));
        if (security){
            String userPrincipal = props.getProperty(Constants.HADOOP_SECURITY_USERPRINCIPAL);
            StringBuilder sb = new StringBuilder();
            sb.append("user.principal=").append(userPrincipal).append(";");
            sb.append("user.keytab=").append(Resource.getPath("user.keytab")).append(";");
            if (!this.url.endsWith(";")){
                this.url += ";";
            }
            this.url += sb.toString();
            System.setProperty("java.security.krb5.conf", Resource.getPath("krb5.conf"));
            System.setProperty(ZOOKEEPER_SERVER_PRINCIPAL_KEY, ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
