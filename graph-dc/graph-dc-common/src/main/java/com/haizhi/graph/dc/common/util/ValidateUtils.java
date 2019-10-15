package com.haizhi.graph.dc.common.util;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Created by chengmo on 2019/6/19.
 */
public class ValidateUtils {

    public static boolean checkDomain(Domain domain, DcInboundDataSuo suo, CudResponse cudResponse) {
        if (domain.invalid()) {
            cudResponse.setRowsErrors(suo.getRows().size());
            cudResponse.setMessage(MessageFormat.format("graph[{0}] not found", suo.getGraph()));
            return false;
        }
        return true;
    }

    public static boolean checkStoreURL(StoreURL storeURL, StoreType storeType, DcInboundDataSuo suo, CudResponse
            cudResponse) {
        if (Objects.isNull(storeURL) || StringUtils.isBlank(storeURL.getStoreVersion())) {
            cudResponse.setMessage(MessageFormat.format(
                    "storeURL is null or storeVersion is blank, graph={0}, storeType={1}",
                    suo.getGraph(), storeType));
            return false;
        }
        return true;
    }
}
