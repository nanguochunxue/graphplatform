package com.haizhi.graph.plugins.flume.sink;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.serialization.EventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.EX_DESC;

/**
 * Created by chengangxiong on 2018/12/19
 */
public class AppendMsgSerializer implements EventSerializer {

    private static final Logger logger = LoggerFactory.getLogger(AppendMsgSerializer.class);

    // for legacy reasons, by default, append a newline to each event written out
    private final String APPEND_NEWLINE = "appendNewline";
    private final boolean APPEND_NEWLINE_DFLT = true;

    private final OutputStream out;
    private final boolean appendNewline;

    private AppendMsgSerializer(OutputStream out, Context ctx){
        this.appendNewline = ctx.getBoolean(APPEND_NEWLINE, APPEND_NEWLINE_DFLT);
        this.out = out;
    }

    @Override
    public void afterCreate() {

    }

    @Override
    public void afterReopen() {

    }

    @Override
    public void write(Event event) throws IOException {
        out.write(event.getBody());
        String exMsg = "    [" + event.getHeaders().get(EX_DESC) + "]";
        out.write(exMsg.getBytes());
        if (appendNewline) {
            out.write('\n');
        }
        out.flush();
    }

    @Override
    public void flush() {

    }

    @Override
    public void beforeClose() {

    }

    @Override
    public boolean supportsReopen() {
        return true;
    }

    public static class Builder implements EventSerializer.Builder {
        @Override
        public EventSerializer build(Context context, OutputStream out) {
            AppendMsgSerializer s = new AppendMsgSerializer(out, context);
            return s;
        }
    }
}
