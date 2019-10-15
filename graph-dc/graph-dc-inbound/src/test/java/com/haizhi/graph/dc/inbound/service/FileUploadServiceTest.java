package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.hdfs.HDFSHelper;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengangxiong on 2019/03/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class FileUploadServiceTest {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private StoreUsageService storeUsageService;

    @Test
    @Transactional
    @Commit
    public void upload() throws IOException {
        File file = new File("/Users/haizhi/Downloads/hello.json");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "hello.json",
                "hello.json",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new FileInputStream(file)
                );

        SysFilePo sysFilePo = fileUploadService.save(multipartFile);
        System.out.println(sysFilePo);
    }

    @Test
    public void read(){
        List<String> sourceList = new ArrayList<>();
        HDFSHelper hdfsHelper = new HDFSHelper(storeUsageService.findStoreURL(1000018L));
        sourceList.add("/user/dmp2/test/task_e826a980-32fa-4de3-96ea-b29b45c55a4b.txt");
        sourceList.forEach(s -> hdfsHelper.readLine(s, System.out::println));
    }
}