package com.haizhi.graph.dc.store.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.model.vo.DcEnvFileVo;
import com.haizhi.graph.dc.core.service.DcEnvFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Api(description = "[环境文件管理]-增删改查")
@RestController
@RequestMapping("/envFile")
public class DcEnvFileController {

    private static final GLog LOG = LogFactory.getLogger(DcEnvFileController.class);

    @Autowired
    private DcEnvFileService dcEnvFileService;

    @ApiOperation(value = "根据环境ID，查找关联的环境文件")
    @GetMapping(value = "/findFilesByEnvId")
    public Response<List<DcEnvFileVo>> findFilesByEnvId(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcEnvFileService.findFilesByEnvId(id);
    }

    @ApiOperation(value = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response<Long> upload(@RequestParam(value = "file", required = true) MultipartFile file) {
        return dcEnvFileService.save(file);
    }

    @ApiOperation(value = "删除文件")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcEnvFileService.delete(id);
    }

    @ApiOperation(value = "下载文件")
    @GetMapping(value = "/download")
    public StreamingResponseBody download(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id, HttpServletResponse response) {
        return dcEnvFileService.download(id, response);
    }
}
