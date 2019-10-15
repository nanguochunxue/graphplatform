package com.haizhi.graph.dc.store.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcStoreAutoUrlQo;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/26
 */
public interface StoreManageService {

    Response autoGenUrl(DcStoreAutoUrlQo dcStoreAutoUrlQo);

    PageResponse findPage(DcStoreQo dcStoreQo);

    Response<List<SysDictPo>> supportedVersion(StoreType storeType);

    Response<String> testConnect(DcStoreSuo dcStoreSuo);

    Response<String> testConnect(String exportUrl);
}
