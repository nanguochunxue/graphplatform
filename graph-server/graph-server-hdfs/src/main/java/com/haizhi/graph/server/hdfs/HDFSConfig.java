package com.haizhi.graph.server.hdfs;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.util.PropertiesUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by chengmo on 2018/4/16.
 */
public class HDFSConfig {
    private Configuration conf = new Configuration();

    public HDFSConfig(String locationConfig) {
        Properties props = PropertiesUtils.load("/" + locationConfig);
        boolean security = BooleanUtils.toBoolean(props.getProperty(Constants.HADOOP_SECURITY_ENABLED));
        if (security) {
            conf.addResource("core-site.xml");
            conf.addResource("hdfs-site.xml");
            // security mode
            if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
                String userPrincipal = props.getProperty(Constants.HADOOP_SECURITY_USERPRINCIPAL);
                String userKeytabFile = Resource.getPath("user.keytab");
                String krb5File = Resource.getPath("krb5.conf");
                System.setProperty("java.security.krb5.conf", krb5File);
                try {
                    LoginUtil.login(userPrincipal, userKeytabFile, krb5File, conf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Configuration getConf() {
        return conf;
    }
}
