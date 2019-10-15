package com.haizhi.graph.search.arango.service.result;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.gdb.model.GdbDataVo;
import com.haizhi.graph.search.api.model.vo.GdbSearchVo;

/**
 * Created by tanghaiyang on 2019/4/16.
 */
public class GdbVoBuilder {

    public static Response<GdbSearchVo> get(GdbDataVo dataVo){
        GdbSearchVo gdbSearchVo = new GdbSearchVo();
        gdbSearchVo.setData(dataVo);
        return Response.success(gdbSearchVo);
    }

}
