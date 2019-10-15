package com.haizhi.graph.dc.inbound.service.impl;

import com.google.common.base.Preconditions;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.dc.core.constant.InboundConstant;
import com.haizhi.graph.dc.core.constant.TaskStatus;
import com.haizhi.graph.dc.inbound.service.FileUploadService;
import com.haizhi.graph.sys.file.constant.StoreType;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import com.haizhi.graph.sys.file.service.SysFileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${graph.dc.inbound.fileUploadDir}")
    private String fileUploadDir;

    @Autowired
    private SysFileService sysFileService;

    @PostConstruct
    public void postConstruct() throws Exception {
        Preconditions.checkArgument(StringUtils.isNotEmpty(fileUploadDir));
        Path path = Paths.get(fileUploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    @Override
    @Transactional
    public List<SysFilePo> save(MultipartFile[] files) {
        Preconditions.checkNotNull(files);
        List<SysFilePo> result = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                SysFilePo sysFilePo = upload(file);
                result.add(sysFilePo);
            }
        } catch (IOException e) {
            throw new UnexpectedStatusException(TaskStatus.FILE_UPLOAD_ERROR, e);
        }
        return result;
    }

    @Override
    public SysFilePo save(MultipartFile file) {
        Preconditions.checkNotNull(file);
        SysFilePo sysFilePo;
        try {
            sysFilePo = upload(file);
        } catch (IOException e) {
            throw new UnexpectedStatusException(TaskStatus.FILE_UPLOAD_ERROR, e);
        }
        return sysFilePo;
    }

    @Override
    public void download(SysFilePo sysFilePo, OutputStream outputStream) throws IOException {
        if (sysFilePo.getStoreType() == StoreType.LOCAL) {
            IOUtils.copy(new FileInputStream(sysFilePo.getUrl()), outputStream);
        } else {
            throw new UnexpectedStatusException(TaskStatus.UNSUPPORTED_STORE_TYPE_TASK_FILE);
        }
    }

    private SysFilePo upload(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String fileType = originalFileName.substring(lastDotIndex);

        String destFileName = InboundConstant.UPLOAD_FILE_PRE + UUID.randomUUID() + fileType;
        //file name will remove extension name
        if (lastDotIndex > 50) {
            throw new UnexpectedStatusException(TaskStatus.FILE_NAME_TOO_LONG);
        }
        Path toUpload = Paths.get(fileUploadDir, destFileName);
        long uploadedByteLength = IOUtils.copy(file.getInputStream(), new FileOutputStream(toUpload.toFile()));
        Preconditions.checkArgument(file.getSize() > 0, new UnexpectedStatusException(TaskStatus.ZERO_SIZE_TASK_FILE));
        Preconditions.checkArgument(uploadedByteLength > 0, new UnexpectedStatusException(TaskStatus.TASK_FILE_UPLOAD_ZERO));
        SysFilePo po = new SysFilePo(originalFileName, file.getSize(), toUpload.toString(), StoreType.LOCAL);
        po.setFileSize(uploadedByteLength);
        return sysFileService.saveOrUpdate(po);
    }
}
