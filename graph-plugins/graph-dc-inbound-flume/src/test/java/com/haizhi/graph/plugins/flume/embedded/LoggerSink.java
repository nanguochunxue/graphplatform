package com.haizhi.graph.plugins.flume.embedded;

import com.google.common.base.Charsets;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chengangxiong on 2018/12/25
 */
public class LoggerSink extends AbstractSink implements Configurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerSink.class);

    @Override
    public Status process() throws EventDeliveryException {

        Channel channel = getChannel();
        Transaction txn = channel.getTransaction();
        try{
            txn.begin();
            System.out.println(new String(channel.take().getBody(), Charsets.UTF_8));
        }catch (Throwable e){
            txn.commit();
            txn.close();
        }
        return Status.READY;
    }

    @Override
    public void configure(Context context) {

    }
}
