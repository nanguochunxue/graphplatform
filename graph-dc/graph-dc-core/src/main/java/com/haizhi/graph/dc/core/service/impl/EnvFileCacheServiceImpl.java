package com.haizhi.graph.dc.core.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import com.haizhi.graph.dc.core.service.DcEnvFileService;
import com.haizhi.graph.dc.core.service.EnvFileCacheService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by chengangxiong on 2019/03/30
 */
@Service
public class EnvFileCacheServiceImpl implements EnvFileCacheService {

    private static final GLog LOG = LogFactory.getLogger(EnvFileCacheServiceImpl.class);

    public final Table<Long, String, EnvFile> envIdFileConfig = HashBasedTable.create();

    @Autowired
    private DcEnvFileService dcEnvFileService;

    @Override
    public Map<String, String> findEnvFile(@NonNull Long envId) {
        Map<String, String> fileMap = new HashMap<>();
        try {
            List<DcEnvFilePo> fileList = dcEnvFileService.findFileListById(envId);
            Map<String, EnvFile> envFileMap = envIdFileConfig.row(envId);
            fileList.stream().forEach(dcEnvFilePo -> {
                String fileName = dcEnvFilePo.getName();
                if (envIdFileConfig.row(envId) == null){
                    generate(dcEnvFilePo);
                }
                if (envIdFileConfig.row(envId).get(fileName) == null){
                    generate(dcEnvFilePo);
                }
                EnvFile old = envFileMap.get(fileName);
                if (old.before(dcEnvFilePo)){
                    old.delete();
                    generate(dcEnvFilePo);
                }
            });
            envFileMap.forEach((s, envFile) -> fileMap.put(s, envFile.getFilePath()));
        } catch (Exception e) {
            LOG.error(e);
        }
        return fileMap;
    }

    @Override
    public void deleteEnvFile(Long envId, String fileName) {
        envIdFileConfig.row(envId).remove(fileName);
    }

    private void generate(DcEnvFilePo dcEnvFilePo) {
        EnvFile envFile = new EnvFile(dcEnvFilePo.getEnvId(), dcEnvFilePo);
        String[] fileNameArray = dcEnvFilePo.getName().split("\\.");
        String tempFileName = fileNameArray[0] + "_" + UUID.randomUUID().toString() + "_";
        try {
            File file = File.createTempFile(tempFileName, "." + fileNameArray[1]);
            file.deleteOnExit();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(dcEnvFilePo.getContent());
            outputStream.flush();
            envFile.setFilePath(file.getPath());
            envIdFileConfig.put(envFile.getEnvId(), dcEnvFilePo.getName(), envFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @EqualsAndHashCode
    private class EnvFile {
        private Long envId;
        private Long envFileId;
        private Date envTime;
        private String filePath;

        public EnvFile(Long envId, DcEnvFilePo dcEnvFilePo) {
            this.envId = envId;
            this.envFileId = dcEnvFilePo.getId();
            this.envTime = dcEnvFilePo.getUpdatedDt() == null ? dcEnvFilePo.getCreatedDt() : dcEnvFilePo.getUpdatedDt();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EnvFile envFile = (EnvFile) o;
            return com.google.common.base.Objects.equal(envId, envFile.envId) &&
                    com.google.common.base.Objects.equal(envFileId, envFile.envFileId);
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(envId, envFileId);
        }

        public boolean before(DcEnvFilePo dcEnvFilePo) {
            Date freshTime = dcEnvFilePo.getUpdatedDt() == null ? dcEnvFilePo.getCreatedDt() : dcEnvFilePo.getUpdatedDt();
            return envTime.before(freshTime);
        }

        public void delete() {
            File file = new File(filePath);
            file.delete();
        }
    }
}