package com.haizhi.graph.server.hbase;

import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.EnvVersion;

/**
 * Created by chengmo on 2019/6/21.
 */
public class StoreURLFactory {


    public static StoreURL createHBase_CDH() {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.16,192.168.1.17,192.168.1.18:2181");
        storeURL.setSecurityEnabled(false);
        storeURL.setUserPrincipal("dmp");
        storeURL.setEnvVersion(EnvVersion.CDH.name());
        return storeURL;
    }

    public static StoreURL createHBase_FIC80() {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.223,192.168.1.224,192.168.1.225:24002");
        storeURL.getFilePath().put("core-site.xml", Resource.getResourcePath("/fi/core-site.xml"));
        storeURL.getFilePath().put("hbase-site.xml", Resource.getResourcePath("/fi/hbase-site.xml"));
        storeURL.getFilePath().put("hdfs-site.xml", Resource.getResourcePath("/fi/hdfs-site.xml"));
        storeURL.getFilePath().put("krb5.conf", Resource.getResourcePath("/fi/krb5.conf"));
        storeURL.getFilePath().put("jaas.conf", Resource.getResourcePath("/fi/jaas.conf"));
        storeURL.getFilePath().put("user.keytab", Resource.getResourcePath("/fi/user.keytab"));
        storeURL.setSecurityEnabled(true);
        storeURL.setUserPrincipal("dmp");
        storeURL.setEnvVersion(EnvVersion.FI.name());
        return storeURL;
    }

    public static StoreURL createHBase_KSYUN() {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.173,192.168.1.174,192.168.1.175:2181");
        storeURL.getFilePath().put("hbase-site.xml", Resource.getResourcePath("/ksyun/hbase-site.xml"));
        storeURL.getFilePath().put("krb5.conf", Resource.getResourcePath("/ksyun/krb5.conf"));
        storeURL.getFilePath().put("user.keytab", Resource.getResourcePath("/ksyun/user.keytab"));
        storeURL.setSecurityEnabled(true);
        storeURL.setUserPrincipal("hbase-ks_hdp@STARGRAPH.CN");
        storeURL.setEnvVersion(EnvVersion.KSYUN.name());
        return storeURL;
    }
}
