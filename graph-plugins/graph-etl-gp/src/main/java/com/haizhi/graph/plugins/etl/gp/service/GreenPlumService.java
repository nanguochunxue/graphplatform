package com.haizhi.graph.plugins.etl.gp.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.model.plugins.etl.gp.EtlGreenPlumQo;

/**
 * Created by chengmo on 2019/4/15.
 */
public interface GreenPlumService {

    Response startExport(EtlGreenPlumQo greenPlumOo);

    Response getExportProgress(EtlGreenPlumQo greenPlumOo);
}
