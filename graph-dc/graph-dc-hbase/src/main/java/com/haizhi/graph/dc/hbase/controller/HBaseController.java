package com.haizhi.graph.dc.hbase.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.dc.hbase.service.HBaseQueryService;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chengmo on 2018/7/19.
 */
@Api(description = "[数据中心-Hbase]查询与删除")
@RestController
@RequestMapping("/api")
public class HBaseController {

    @Autowired
    private HBaseQueryService hBaseQueryService;

    @ApiOperation(value = "根据object_key查询多表记录", notes = "输入 {\"graph\": \"hbase_store\", \"schmas\": {\"graph_ccb_dev:te_repayment\": [\"006F52E9102A8D3BE2FE5614F42BA989\"]}}")
    @PostMapping(path = "/searchByKeys", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    Response<KeySearchVo> searchByKeys(@RequestBody KeySearchQo searchQo){
        return hBaseQueryService.searchByKeys(searchQo);
    }
}
