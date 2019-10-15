package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengangxiong on 2019/05/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcTaskMetaServiceTest {

    @Autowired
    private DcTaskMetaService dcTaskMetaService;

    @Test
    public void mapping(){
        List<DcTaskMetaPo> meta = dcTaskMetaService.findAllByTaskId(118L);
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("fieldA", "aaa");
        rowData.put("fieldB", "bbb");
        Map<String, Object> res = interceptByFieldMapping(rowData, meta);
        System.out.println(res.toString());
    }

    private Map<String, Object> interceptByFieldMapping(Map<String, Object> rowData, List<DcTaskMetaPo> fieldMapping) {
        if (Objects.nonNull(fieldMapping) && !fieldMapping.isEmpty()){
            Map<String, Object> afterModify = new HashMap<>();
            fieldMapping.stream().forEach(dcTaskMetaPo -> {
                switch (dcTaskMetaPo.getType()){
                    case SEQ:
                        break;
                    case FIELD:
                        String srcField = dcTaskMetaPo.getSrcField();
                        String dstField = dcTaskMetaPo.getDstField();
                        if(rowData.containsKey(srcField)){
                            Object srcValue = rowData.remove(srcField);
                            if (!StringUtils.isEmpty(dstField)){
                                afterModify.put(dstField, srcValue);
                            }
                        }
                        break;
                    default:
                        throw new RuntimeException("not supported taskMetaType : " + dcTaskMetaPo.getType());
                }
            });
            return afterModify;
        }
        return rowData;
    }
}