package com.haizhi.graph.dc.inbound.api.validator;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/04/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class ApiInboundValidatorTest {

    private static final GLog LOG = LogFactory.getLogger(ApiInboundValidatorTest.class);

    @Autowired
    private ApiInboundValidator apiInboundValidator;

    @Test
    public void test() {
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setGraph("graph_chengangxiong");
        suo.setSchema("schema_my_edge");
        suo.setOperation(GOperation.CREATE_OR_UPDATE);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, 14);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, 61);
        List<Map<String, Object>> rows = createData();
        suo.setRows(rows);
//        GLogBuffer buffer = new GLogBuffer(100);

        ValidatorResult validatorResult = new ValidatorResult(suo.getRows().size());
        boolean res = apiInboundValidator.doValidate(suo, validatorResult);
        System.out.println(validatorResult.getBuffer().getLines());
        assert !res;
    }

    @Test
    public void test2() {
        String api = FileUtils.readTxtFile("api/DcInboundDataSuo.json");
        DcInboundDataSuo suo = JSONObject.parseObject(api, DcInboundDataSuo.class);
        ValidatorResult result = new ValidatorResult(100);
        AbstractValidator validateHandler = ValidatorBuilder.build().add(ApiInboundValidator.class).get();
        if (!validateHandler.validate(suo, result)) {
            DcInboundErrorInfo errorInfo = new DcInboundErrorInfo(suo.getGraph(), suo.getSchema(),
                    DcInboundErrorInfo.ErrorType.CHECK_ERROR);
            errorInfo.setMsg(result.getBuffer().getLines());
            errorInfo.setInboundDataSuoInfo(suo, result.getErrorRows());
            LOG.info("valid false, graph={0}, schema={1}, errorMsg={2}",
                    suo.getGraph(), suo.getSchema(), result.getAllErrorMsg());
        }
    }

    private List<Map<String, Object>> createData() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put(Keys.FROM_KEY, "a1dxxx");
        m1.put(Keys.TO_KEY, "a1/dxxx");
        m1.put(Keys.OBJECT_KEY, "aa");
        m1.put(Keys.CTIME, "2019-01-01 10:20:10");
        rows.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put(Keys.FROM_KEY, "b1/cxxx");
        m2.put(Keys.TO_KEY, "b1/cxxxx");
        m2.put(Keys.OBJECT_KEY, "bb");
        m2.put(Keys.CTIME, "2019-01-01 10:20:10");
        rows.add(m2);
        return rows;
    }
}