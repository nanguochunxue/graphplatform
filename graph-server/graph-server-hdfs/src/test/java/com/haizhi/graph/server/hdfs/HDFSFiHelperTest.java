package com.haizhi.graph.server.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.IOException;


/**
 * Created by chengangxiong on 2019/05/15
 */
public class HDFSFiHelperTest {

    private String configPath = "/Users/haizhi/IdeaProjects/graph-dev/graph/graph-server/graph-server-hdfs/src/test/resources/fi/";

    @Test
    public void test() throws IOException {
        Configuration conf = new Configuration();
        conf.addResource(new Path(configPath + "hdfs-site.xml"));
        conf.addResource(new Path(configPath + "core-site.xml"));

        if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
            LoginUtil.login("dmp", configPath + "user.keytab", configPath + "krb5.conf", conf);
        }
        FileSystem fs = FileSystem.get(conf);
        boolean res = fs.exists(new Path("/user/dmp2/test"));
        System.out.println(res);
    }
}
