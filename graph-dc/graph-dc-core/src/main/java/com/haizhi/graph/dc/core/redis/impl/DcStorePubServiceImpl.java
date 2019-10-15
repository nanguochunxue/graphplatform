package com.haizhi.graph.dc.core.redis.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.common.redis.channel.ChannelKeys;
import com.haizhi.graph.dc.core.dao.DcGraphStoreDao;
import com.haizhi.graph.dc.core.dao.DcStoreDao;
import com.haizhi.graph.dc.core.model.po.DcGraphStorePo;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2019/6/25.
 */
@Service
public class DcStorePubServiceImpl implements DcStorePubService {

    private static final GLog LOG = LogFactory.getLogger(DcStorePubServiceImpl.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private DcGraphStoreDao dcGraphStoreDao;

    @Autowired
    private DcStoreDao dcStoreDao;

    @Override
    public boolean publishByStoreId(@NonNull Long storeId) {
        try {
            List<DcGraphStorePo> storePoList = dcGraphStoreDao.findByStoreId(storeId);
            boolean success = publish(storePoList);
            LOG.info("publish by storeId={0}, success={1}", storeId, success);
            return success;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean publishByEnvId(@NonNull Long envId) {
        try {
            Set<Long> storeIds = dcStoreDao.findByEnvId(envId).stream()
                    .map(DcStorePo::getId).collect(Collectors.toSet());
            List<DcGraphStorePo> storePoList = dcGraphStoreDao.findByStoreIdIn(storeIds);
            boolean success = publish(storePoList);
            LOG.info("publish by envId={0}, success={1}", envId, success);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean publishByGraph(String graph) {
        try {
            List<DcGraphStorePo> storePoList = dcGraphStoreDao.findByGraph(graph);
            boolean success = publish(storePoList);
            LOG.info("publish by graph={0}, success={1}", graph, success);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    private boolean publish(@NonNull List<DcGraphStorePo> dcGraphStorePos) {
        boolean success = true;
        Set<String> set = new HashSet<>();
        for (DcGraphStorePo storePo : dcGraphStorePos) {
            try {
                String message = storePo.getGraph() + "#" + storePo.getStoreType().getName();
                if (set.add(message)) {
                    success = success & redisService.publish(ChannelKeys.DC_STORE, message);
                    LOG.info("public store=[{0}] fresh ,result=[{1}]", storePo.getId(), message);
                }
            } catch (Exception e) {
                LOG.error("public store fresh message error", e);
                success = false;
            }
        }
        return success;
    }

}
