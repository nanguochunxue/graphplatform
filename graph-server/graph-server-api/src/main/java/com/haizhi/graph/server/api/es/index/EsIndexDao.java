package com.haizhi.graph.server.api.es.index;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.bean.ScriptSource;
import com.haizhi.graph.server.api.es.index.bean.Source;

import java.util.List;

/**
 * Created by chengmo on 2017/12/28.
 */
public interface EsIndexDao {

    void testConnect(StoreURL storeURL);

    boolean existsIndex(StoreURL storeURL, String index);

    boolean existsType(StoreURL storeURL, String index, String type);

    boolean createIndex(StoreURL storeURL, String index);

    boolean deleteIndex(StoreURL storeURL, String index);

    boolean createType(StoreURL storeURL, String index, String type);

    CudResponse bulkUpsert(StoreURL storeURL, String index, String type, List<Source> sourceList);

    CudResponse bulkScriptUpsert(StoreURL storeURL, String index, String type, List<ScriptSource> sources);

    CudResponse delete(StoreURL storeURL, String index, String type, List<Source> sources);
}
