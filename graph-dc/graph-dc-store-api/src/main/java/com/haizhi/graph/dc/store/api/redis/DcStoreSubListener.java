package com.haizhi.graph.dc.store.api.redis;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.RedisSubContainer;
import com.haizhi.graph.common.redis.RedisSubListener;
import com.haizhi.graph.common.redis.channel.ChannelKeys;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Create by zhoumingbing on 2019-06-24
 */
@Service
public class DcStoreSubListener implements RedisSubListener {

    private static final GLog LOG = LogFactory.getLogger(DcStoreSubListener.class);

    @Autowired
    private RedisSubContainer redisSubContainer;

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @PostConstruct
    public void init() {
        redisSubContainer.addSubListener(ChannelKeys.DC_STORE, this);
    }


    @Override
    public void receiveMessage(String message, String channel) {
        try {
            LOG.info("Success to receive [channel={0},message={1}]", channel, message);
            String graph = StringUtils.substringBefore(message, "#");
            dcMetadataCache.refresh(StringUtils.removeAll(graph, "\""));
            storeUsageService.refresh(StringUtils.removeAll(message, "\""));
        } catch (Exception e) {
            LOG.error(e);
        }
    }

}
