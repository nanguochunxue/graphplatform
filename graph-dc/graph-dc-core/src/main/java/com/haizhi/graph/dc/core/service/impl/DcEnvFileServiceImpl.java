package com.haizhi.graph.dc.core.service.impl;

import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.StoreStatus;
import com.haizhi.graph.dc.core.dao.DcEnvFileDao;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import com.haizhi.graph.dc.core.model.po.QDcEnvFilePo;
import com.haizhi.graph.dc.core.model.vo.DcEnvFileVo;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import com.haizhi.graph.dc.core.service.DcEnvFileService;
import com.haizhi.graph.dc.core.service.EnvFileCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Service
public class DcEnvFileServiceImpl extends JpaBase implements DcEnvFileService {

    private static final GLog LOG = LogFactory.getLogger(DcEnvFileServiceImpl.class);

    @Autowired
    private DcEnvFileDao dcEnvFileDao;

    @Autowired
    private EnvFileCacheService envFileCacheService;

    @Autowired
    private DcStorePubService dcStorePubService;

    @Override
    public Response<List<DcEnvFileVo>> findFilesByEnvId(Long envId) {
        if (Objects.isNull(envId)) {
            return Response.success();
        }
        try {
            List<DcEnvFilePo> fileList = findFileListById(envId);
            if (fileList.isEmpty()) {
                return Response.success();
            }
            List<DcEnvFileVo> voList = fileList.stream().map(DcEnvFileVo::new).collect(Collectors.toList());
            return Response.success(voList);
        } catch (Exception e) {
            LOG.error(e);
            throw new UnexpectedStatusException(StoreStatus.ENV_FILE_FIND_FAIL);
        }
    }

    @Override
//    @Cacheable(cacheNames = RKeys.DC_ENV_FILE, key = "#p0")
    public List<DcEnvFilePo> findFileListById(Long envId) {
        return dcEnvFileDao.findAllByEnvId(envId);
    }

    @Override
//    @CacheEvict(cacheNames = RKeys.DC_ENV_FILE, key = "#p0")
    public void updateEnvId(Long envId, List<Long> envFileId) {
        QDcEnvFilePo envFileTable = QDcEnvFilePo.dcEnvFilePo;
        jpa.update(envFileTable).set(envFileTable.envId, envId).where(envFileTable.id.in(envFileId)).execute();
    }

    @Override
//    @CacheEvict(cacheNames = RKeys.DC_ENV_FILE, key = "#p0")
    public void deleteById(Long envId, List<Long> envFileId) {
        QDcEnvFilePo envFileTable = QDcEnvFilePo.dcEnvFilePo;
        jpa.delete(envFileTable).where(envFileTable.id.in(envFileId)).execute();
    }

    @Override
    public Response delete(Long envFileId) {
        try {
            DcEnvFilePo dcEnvFile = dcEnvFileDao.findOne(envFileId);
            if (Objects.isNull(dcEnvFile)) {
                throw new UnexpectedStatusException(StoreStatus.ENV_FILE_NOT_EXISTS);
            }
            delete(dcEnvFile.getEnvId(), envFileId);
        } catch (Exception e) {
            LOG.error(e);
            throw new UnexpectedStatusException(StoreStatus.ENV_FILE_DELETE_FAIL);
        }
        return Response.success();
    }

    @Override
//    @CacheEvict(cacheNames = RKeys.DC_ENV_FILE, key = "#p0")
    public void delete(Long envId, Long envFileId) {
        DcEnvFilePo dcEnvFile = dcEnvFileDao.findOne(envFileId);
        dcEnvFileDao.delete(envFileId);
        envFileCacheService.deleteEnvFile(dcEnvFile.getEnvId(), dcEnvFile.getName());
        dcStorePubService.publishByEnvId(dcEnvFile.getEnvId());
    }

    @Override
    public StreamingResponseBody download(Long id, HttpServletResponse response) {
        DcEnvFilePo dcEnvFile = dcEnvFileDao.getOne(id);
        if (Objects.isNull(dcEnvFile)) {
            throw new UnexpectedStatusException(StoreStatus.ENV_FILE_NOT_EXISTS);
        }
        byte[] contentData = dcEnvFile.getContent();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + dcEnvFile.getName() + "\"");
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(contentData)) {
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, nRead);
                }
            }
        };
    }

    @Override
    public Response<Long> save(MultipartFile file) {
        try {
            DcEnvFilePo dcEnvFilePo = new DcEnvFilePo(file);
            dcEnvFilePo = dcEnvFileDao.save(dcEnvFilePo);
            if (Objects.nonNull(dcEnvFilePo.getEnvId())) {
                dcStorePubService.publishByEnvId(dcEnvFilePo.getEnvId());
            }
            return Response.success(dcEnvFilePo.getId());
        } catch (Exception e) {
            LOG.error(e);
            throw new UnexpectedStatusException(StoreStatus.ENV_FILE_UPLOAD_FAIL);
        }
    }
}