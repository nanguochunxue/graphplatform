package com.haizhi.graph.engine.flow.tools.es;

import com.google.common.collect.Maps;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.ScriptSource;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.es.client.EsClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by chengmo on 2018/3/12.
 */
@Repository
public class EsBulkLoader {

    private static final GLog LOG = LogFactory.getLogger(EsBulkLoader.class);
    private static final int PAGE_SIZE = 5000;
    private static final int PAGE_SIZE_SCRIPT = 5000;

    private static EsClient esClient;
    private static EsIndexDao esIndexDao;
    @Value("${es.cluster.name:graph}")
    private String clusterName;
    @Value("${es.cluster.url:}")
    private String clusterUrl;

    private ExecutorService executor;
    private Map<String, Future<Boolean>> futureMap;
    private Map<String, EsBulkSource> bulkSources = new LinkedHashMap<>();

    public EsBulkLoader() {
    }

    public EsBulkLoader(String clusterName, String clusterUrl) {
        this.clusterName = clusterName;
        this.clusterUrl = clusterUrl;
        this.initialize();
    }

    @PostConstruct
    public void connectDefault() {
        this.initialize();
    }

    /**
     * Add bulk source doc.
     *
     * @param source
     */
    public void addBulkSource(EsBulkSource source) {
        if (source == null) {
            return;
        }
        EsBulkSource existed = bulkSources.get(source.getKey());
        if (existed == null) {
            bulkSources.put(source.getKey(), source);
            return;
        }
        existed.getSources().addAll(source.getSources());
    }

    /**
     * Bulk docs wait for completion.
     *
     * @return
     */
    public boolean waitForCompletion() {
        boolean success = true;
        try {
            int totalPage = this.getTotalPage();
            if (totalPage == 0) {
                return success;
            }
            if (totalPage == 1){
                EsBulkSource source = bulkSources.values().iterator().next();
                String index = source.getIndex();
                String type = source.getTypeName();
                if (source.getSources() != null){
                    return esIndexDao.bulkUpsert(null, index, type, source.getSources()).isSuccess();
                } else {
                    return esIndexDao.bulkScriptUpsert(null, index, type, source.getScriptSources()).isSuccess();
                }
            }

            // pool
            this.executor = Executors.newFixedThreadPool(totalPage);
            this.futureMap = Maps.newLinkedHashMap();

            // submit sources
            for (EsBulkSource ebs : bulkSources.values()) {
                int pageCount = ebs.getSourcesPageCount(PAGE_SIZE);
                for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
                    List<Source> pageSources = ebs.getPageSources(pageNo, PAGE_SIZE);
                    BulkUpdater updater = new BulkUpdater(ebs.getIndex(), ebs.getTypeName(), pageSources);
                    Future<Boolean> future = executor.submit(updater);
                    futureMap.put(ebs.getKey().concat(String.valueOf(pageNo)), future);
                }
            }

            // submit script sources
            for (EsBulkSource ebs : bulkSources.values()) {
                int pageCount = ebs.getScriptSourcesPageCount(PAGE_SIZE_SCRIPT);
                for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
                    List<ScriptSource> pageSources = ebs.getPageScriptSources(pageNo, PAGE_SIZE_SCRIPT);
                    BulkUpdater updater = new BulkUpdater(ebs.getIndex(), ebs.getTypeName(), pageSources, true);
                    Future<Boolean> future = executor.submit(updater);
                    futureMap.put(ebs.getKey().concat(String.valueOf(pageNo)), future);
                }
            }

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
            LOG.info("Execute service has been completed!");

            for (Map.Entry<String, Future<Boolean>> entry : futureMap.entrySet()) {
                Boolean isSucceeded = entry.getValue().get();
                if (!isSucceeded) {
                    success = false;
                    break;
                }
            }
        } catch (Exception e) {
            success = false;
            LOG.error(e);
        } finally {
            this.clearSources();
        }
        return success;
    }

    public void clearSources(){
        this.bulkSources.clear();
    }

    /**
     * Cleanup.
     */
    public void cleanup(){
        if (esClient != null){
            esClient.cleanup();
        }
    }

    public static class EsBulkSource {
        private String index;
        private String typeName;
        private List<Source> sources;
        private List<ScriptSource> scriptSources;

        public EsBulkSource(String index, String typeName) {
            if (StringUtils.isAnyBlank(index, typeName)) {
                throw new IllegalArgumentException();
            }
            this.index = index;
            this.typeName = typeName;
        }

        public String getKey() {
            return StringUtils.join(index, "#", typeName);
        }

        public int getSourcesPageCount(int pageSize) {
            if (sources == null){
                return 0;
            }
            return (sources.size() + pageSize - 1) / pageSize;
        }

        public int getScriptSourcesPageCount(int pageSize) {
            if (scriptSources == null){
                return 0;
            }
            return (scriptSources.size() + pageSize - 1) / pageSize;
        }

        public List<Source> getPageSources(int pageNo, int pageSize) {
            return this.getPage(pageNo, pageSize, sources);
        }

        public List<ScriptSource> getPageScriptSources(int pageNo, int pageSize) {
            return this.getPage(pageNo, pageSize, scriptSources);
        }

        private <T> List<T> getPage(int pageNo, int pageSize, List<T> sources) {
            int fromIndex = (pageNo - 1) * pageSize;
            if (fromIndex >= sources.size()) {
                return Collections.emptyList();
            }
            int toIndex = pageNo * pageSize;
            if (toIndex >= sources.size()) {
                toIndex = sources.size();
            }
            return sources.subList(fromIndex, toIndex);
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public List<Source> getSources() {
            return sources;
        }

        public void setSources(List<Source> sources) {
            this.sources = sources;
        }

        public List<ScriptSource> getScriptSources() {
            return scriptSources;
        }

        public void setScriptSources(List<ScriptSource> scriptSources) {
            this.scriptSources = scriptSources;
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private int getTotalPage() {
        int totalPage = 0;
        for (EsBulkSource source : bulkSources.values()) {
            totalPage += source.getSourcesPageCount(PAGE_SIZE);
            totalPage += source.getScriptSourcesPageCount(PAGE_SIZE_SCRIPT);
        }
        return totalPage;
    }

    private static class BulkUpdater implements Callable<Boolean> {
        private String index;
        private String typeName;
        private List<Source> sources;
        private List<ScriptSource> scriptSources;
        private boolean script;

        public BulkUpdater(String index, String typeName, List<Source> sources) {
            this.index = index;
            this.typeName = typeName;
            this.sources = sources;
        }

        public BulkUpdater(String index, String typeName, List<ScriptSource> scriptSources, boolean script) {
            this.index = index;
            this.typeName = typeName;
            this.scriptSources = scriptSources;
            this.script = true;
        }

        @Override
        public Boolean call() throws Exception {
            if (script){
                return esIndexDao.bulkScriptUpsert(null, index, typeName, scriptSources).isSuccess();
            }
            return esIndexDao.bulkUpsert(null, index, typeName, sources).isSuccess();
        }
    }

    private void initialize() {
        if (esIndexDao != null) {
            return;
        }
        esClient = new EsClient();
//        esClient.setClusterName(clusterName);
//        esClient.setClusterUrl(clusterUrl);
//        esClient.connectDefault();
//        esIndexDao = new EsIndexDaoImpl();
//        esIndexDao.setEsClient(esClient);
    }
}
