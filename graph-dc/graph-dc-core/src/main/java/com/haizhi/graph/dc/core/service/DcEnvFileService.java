package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import com.haizhi.graph.dc.core.model.vo.DcEnvFileVo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by chengangxiong on 2019/03/22
 */
public interface DcEnvFileService {

    Response<List<DcEnvFileVo>> findFilesByEnvId(Long envId);

    List<DcEnvFilePo> findFileListById(Long envId);

    void updateEnvId(Long envId, List<Long> envFileId);

    void deleteById(Long envId, List<Long> envFileId);

    Response delete(Long id);

    void delete(Long envId, Long envFileId);

    StreamingResponseBody download(Long id, HttpServletResponse response);

    Response<Long> save(MultipartFile file);
}
