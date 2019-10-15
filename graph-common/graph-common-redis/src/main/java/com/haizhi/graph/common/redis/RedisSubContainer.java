package com.haizhi.graph.common.redis;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Created by chengmo on 2019/6/24.
 */
@Component
public class RedisSubContainer {
    private static final GLog LOG = LogFactory.getLogger(RedisSubContainer.class);
    private static final String DEFAULT_LISTENER_METHOD = "receiveMessage";

    @Autowired
    private RedisMessageListenerContainer redisListenerContainer;

    /**
     * Added a redis subscribe message listener.
     *
     * @param channel
     * @param listener
     */
    public void addSubListener(String channel, RedisSubListener listener) {
        try {
            MessageListenerAdapter adapter = new MessageListenerAdapter(listener, DEFAULT_LISTENER_METHOD);
            adapter.afterPropertiesSet();
            redisListenerContainer.addMessageListener(adapter, new ChannelTopic(channel));
            LOG.info("Success to subscribe redis message on channel={0}", channel);
        } catch (Exception e) {
            LOG.error("Failed to subscribe redis message on channel={0}", e, channel);
        }
    }

}
