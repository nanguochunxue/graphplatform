package com.haizhi.graph.common.concurrent.action;


import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;

/**
 * Created by chengmo on 2018/2/5.
 */
public class ActionSupplier<V> extends AbstractCallable<V> {

    private static final GLog LOG = LogFactory.getLogger(ActionSupplier.class);

    private ActionListener<V> listener;

    public ActionSupplier(ActionListener<V> listener) {
        this.listener = listener;
    }

    @Override
    protected V execute() throws Exception {
        return listener.doExecute();
    }

    @Override
    public void onFailure(Throwable t) {
        LOG.error(t);
    }

}

