package com.haizhi.graph.common.log;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chengmo on 2018/10/25.
 */
public class GLogBuffer {
    private static final int DEFAULT_BUFFER_SIZE = 1000;
    private List<BufferLine> buffer;
    private int bufferSize;

    public GLogBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public GLogBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new LinkedList<>();
    }

    public void clear() {
        buffer.clear();
    }

    public int getMaxNrLines() {
        return bufferSize;
    }

    public void setMaxNrLines(int maxNrLines) {
        this.bufferSize = maxNrLines;
    }

    public int getNrLines() {
        return buffer.size();
    }

    public int size() {
        return buffer.size();
    }

    public void addLine(Object message) {
        if (message == null) {
            return;
        }
        doAppend(new LoggingEvent(message));
    }

    public void appendBuffer(List<Object> lines) {
        for (int i = 0; i < lines.size(); i++) {
            doAppend(lines.get(i));
        }
    }

    public void appendBuffer(GLogBuffer logBuffer) {
        List<BufferLine> lines = logBuffer.getBufferLines();
        for (int i = 0; i < lines.size(); i++) {
            BufferLine line = lines.get(i);
            doAppend(line.getEvent().getMessage());
        }
    }

    public List<Object> getLines() {
        List<Object> lines = new ArrayList<>();
        for (int i = 0; i < buffer.size(); i++) {
            BufferLine line = buffer.get(i);
            lines.add(line.getEvent().getMessage());
        }
        return lines;
    }

    public List<BufferLine> getBufferLines() {
        return buffer;
    }

    private void doAppend(Object message) {
        this.doAppend(new LoggingEvent(message));
    }

    private void doAppend(LoggingEvent event) {
        buffer.add(new BufferLine(event));
        while (bufferSize > 0 && buffer.size() > bufferSize) {
            buffer.remove(0);
        }
    }

    public static class BufferLine {
        private static AtomicInteger sequence = new AtomicInteger(0);
        private int nr;
        private LoggingEvent event;

        public BufferLine(LoggingEvent event) {
            this.event = event;
            this.nr = sequence.incrementAndGet();
        }

        public int getNr() {
            return nr;
        }

        public LoggingEvent getEvent() {
            return event;
        }

        @Override
        public String toString() {
            return event.toString();
        }
    }

    @Data
    public static class LoggingEvent {
        private Object message;
        private long timeStamp;

        public LoggingEvent() {
            this(null, System.currentTimeMillis());
        }

        public LoggingEvent(Object message) {
            this(message, System.currentTimeMillis());
        }

        public LoggingEvent(Object message, long timeStamp) {
            super();
            this.message = message;
            this.timeStamp = timeStamp;
        }
    }
}
