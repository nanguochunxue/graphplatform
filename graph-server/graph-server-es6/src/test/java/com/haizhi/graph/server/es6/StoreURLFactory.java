package com.haizhi.graph.server.es6;

import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.server.api.bean.StoreURL;

/**
 * Created by chengmo on 2019/6/21.
 */
public class StoreURLFactory {

    public static StoreURL createEs_FIC80() {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.223:24148,192.168.1.224:24148,192.168.1.225:24148");
        storeURL.getFilePath().put("krb5.conf", Resource.getResourcePath("/fi/krb5.conf"));
        storeURL.getFilePath().put("jaas.conf", Resource.getResourcePath("/fi/jaas.conf"));
        storeURL.getFilePath().put("user.keytab", Resource.getResourcePath("/fi/user.keytab"));
        storeURL.setSecurityEnabled(true);
        storeURL.setUserPrincipal("dmp");
        return storeURL;
    }

    public static StoreURL createEs_FIC_release() {
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.223:24148,192.168.1.224:24148,192.168.1.225:24148");
        storeURL.getFilePath().put("krb5.conf", Resource.getResourcePath("/fi_release/krb5.conf"));
        storeURL.getFilePath().put("jaas.conf", Resource.getResourcePath("/fi_release/jaas.conf"));
        storeURL.getFilePath().put("user.keytab", Resource.getResourcePath("/fi_release/user.keytab"));
        storeURL.setSecurityEnabled(true);
        storeURL.setUserPrincipal("dmp");
        return storeURL;
    }

    public static StoreURL createEs_KSYUN() {
        StoreURL storeURL = new StoreURL();
        //storeURL.setUrl("127.0.0.1:9200");
        storeURL.setUrl("192.168.1.190:9200");
        return storeURL;
    }
}
