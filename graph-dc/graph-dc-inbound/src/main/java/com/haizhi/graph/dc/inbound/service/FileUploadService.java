package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.sys.file.model.po.SysFilePo;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by chengangxiong on 2019/01/29
 */
public interface FileUploadService {

    List<SysFilePo> save(MultipartFile[] files);

    SysFilePo save(MultipartFile file);

    void download(SysFilePo sysFilePo, OutputStream outputStream) throws IOException;
}
