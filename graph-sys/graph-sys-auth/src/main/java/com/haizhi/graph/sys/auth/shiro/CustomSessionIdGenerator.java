package com.haizhi.graph.sys.auth.shiro;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.auth.shiro.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by liulu on 2019/7/4.
 */
public class CustomSessionIdGenerator implements SessionIdGenerator {

    private static final GLog LOG = LogFactory.getLogger(CustomSessionIdGenerator.class);
    /**
     * Generates a new ID to be applied to the specified {@code Session} instance.
     *
     * @param session the {@link Session} instance to which the ID will be applied.
     * @return the id to assign to the specified {@link Session} instance before adding a record to the EIS data store.
     */
    @Override
    public Serializable generateId(Session session) {
        String sessionId = WebUtils.getCurrentRequestSessionId();
        if (StringUtils.isBlank(sessionId)){
            sessionId = UUID.randomUUID().toString();
        }
        return sessionId;
    }
}
