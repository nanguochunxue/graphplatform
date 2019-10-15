package com.haizhi.graph.server.kafka.security;

import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.log.GLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by chengmo on 2017/10/31.
 */
@Component
public class KafkaSecurityInitializer {

    private static final GLog LOG = new GLog(KafkaSecurityInitializer.class);

    @Value("${graph.hadoop.security.enabled:false}")
    private boolean securityEnabled;

    @Value("${graph.hadoop.security.user-principal:}")
    private String userPrincipal;

    @PostConstruct
    public void securityPrepare(){
        if (securityEnabled) {
            try {
                LOG.info("Security prepare start.");

                // Loading krb5.conf + user.keytab
                String krbFile = Resource.getPath("krb5.conf");
                String userKeyTableFile = Resource.getPath("user.keytab");

                // For windows
                userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
                krbFile = krbFile.replace("\\", "\\\\");

                LoginUtil.setKrb5Config(krbFile);
                LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
                LoginUtil.setJaasFile(userPrincipal, userKeyTableFile);
            } catch (IOException e) {
                LOG.error("Security prepare failure.");
                LOG.error("The IOException occured.", e);
                return;
            }
            LOG.info("Security prepare success.");
        }
    }


}