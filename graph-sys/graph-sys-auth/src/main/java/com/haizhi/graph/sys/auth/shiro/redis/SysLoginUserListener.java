package com.haizhi.graph.sys.auth.shiro.redis;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.RedisSubContainer;
import com.haizhi.graph.common.redis.RedisSubListener;
import com.haizhi.graph.common.redis.channel.ChannelKeys;
import com.haizhi.graph.sys.auth.service.SysLoginUserCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by chengmo on 2019/6/24.
 */
@Component
public class SysLoginUserListener implements RedisSubListener {
    private static final GLog LOG = LogFactory.getLogger(SysLoginUserListener.class);

    @Autowired
    private RedisSubContainer redisSubContainer;
    @Autowired
    private SysLoginUserCache sysLoginUserCache;

    @PostConstruct
    public void init() {
        redisSubContainer.addSubListener(ChannelKeys.SYS_LOGIN_USER, this);
    }

    @Override
    public void receiveMessage(String message, String channel) {
        try {
            LOG.info("Success to receive [channel={1}, message={1}]", channel, message);
            String msg = StringUtils.removeAll(message, "\"");
            sysLoginUserCache.refresh(Long.valueOf(msg));
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
