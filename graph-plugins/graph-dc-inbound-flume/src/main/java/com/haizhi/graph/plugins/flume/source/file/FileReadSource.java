package com.haizhi.graph.plugins.flume.source.file;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.plugins.flume.BatchApplication;
import com.haizhi.graph.plugins.flume.embedded.EmbeddedAgent;
import com.haizhi.graph.plugins.flume.source.file.core.*;
import com.haizhi.graph.plugins.flume.source.file.model.FileType;
import com.haizhi.graph.plugins.flume.source.file.stage.AbstractStage;
import com.haizhi.graph.plugins.flume.source.file.util.FileParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.mortbay.util.ajax.JSON;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.*;

/**
 * Created by chengmo on 2018/8/28.
 */
public class FileReadSource extends AbstractStage {
    private static final GLog LOG = LogFactory.getLogger(FileReadSource.class);
    private final int maxLineSize;

    private String filePath;
    private String graph;
    private EmbeddedAgent agent;

    public FileReadSource(String graph, EmbeddedAgent agent, BatchApplication.Context context){
        String dataFilePath = context.getString("dataFilePath");
        Preconditions.checkNotNull(dataFilePath, "dataFilePath must specify");
        this.filePath = dataFilePath;
        this.graph = graph;
        this.agent = agent;
        this.maxLineSize = context.getInteger(MAX_LINE_SIZE, DEFAULT_MAX_LINE_SIZE);
    }

    @Override
    @SuppressWarnings("all")
    protected Long doCall() {
        Long eventCount = 0L;
        try {
            DataFileScanner fileScanner = new DataFileScanner(filePath);
            Map<String, DataFileSet> dataFiles = fileScanner.scan();
            for (DataFileSet fileSet : dataFiles.values()) {
                String schema = fileSet.getSchema();
                List<File> files = fileSet.getFiles();
                for (File file : files) {
                    int lineCounter = 0;
                    long pos = 0L;
                    FileType fileType = checkFileType(file.getName());
                    while (true) {
                        Map<String, Object> res = FileParser.readLines(file, Constants.UTF8, pos, maxLineSize);
                        if (MapUtils.isEmpty(res)) break;
                        List<Map<String, Object>> lines = JSONUtils.jsonToListMap(res.get("lines").toString());
                        if (CollectionUtils.isNotEmpty(lines)) {
                            Map<String, String> header = Maps.newHashMap();
                            header.put(HEADER_GRAPH, graph);
                            header.put(HEADER_SCHEMA, schema);
                            Event event = EventBuilder.withBody(JSON.toString(lines).getBytes(), header);
                            agent.put(event);


                            eventCount ++;
                            lineCounter += lines.size();
                            if (lines.size() < maxLineSize) break;
                        } else break;
                        pos = (Long) res.get("pos");
                    }
                    LOG.info(DateUtils.formatLocal(System.currentTimeMillis()) + " | " + file.getPath() + " | count : " + lineCounter);
                }
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return eventCount;
    }

    private FileType checkFileType(String filename){
        if( filename.endsWith(".csv")) return FileType.CSV;
        else return FileType.JSON;
    }
}
