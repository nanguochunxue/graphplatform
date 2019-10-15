package com.haizhi.graph.dc.core.redis;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.RedisSubContainer;
import com.haizhi.graph.common.redis.RedisSubListener;
import com.haizhi.graph.common.redis.channel.ChannelKeys;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by chengmo on 2019/6/24.
 */
@Component
public class DcSubListener implements RedisSubListener {
    private static final GLog LOG = LogFactory.getLogger(DcSubListener.class);

    @Autowired
    private RedisSubContainer redisSubContainer;
    @Autowired
    private DcMetadataCache dcMetadataCache;

    @PostConstruct
    public void init(){
        redisSubContainer.addSubListener(ChannelKeys.DC_METADATA, this);
    }

    @Override
    public void receiveMessage(String message, String channel) {
        try {
            LOG.info("Success to receive [channel={1}, message={1}]", channel, message);
            dcMetadataCache.refresh(StringUtils.removeAll(message, "\""));
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
