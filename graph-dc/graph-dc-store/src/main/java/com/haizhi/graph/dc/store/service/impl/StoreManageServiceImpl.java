package com.haizhi.graph.dc.store.service.impl;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.constant.CoreStatus;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import com.haizhi.graph.dc.core.model.po.QDcEnvPo;
import com.haizhi.graph.dc.core.model.po.QDcStorePo;
import com.haizhi.graph.dc.core.model.qo.DcStoreAutoUrlQo;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.core.model.vo.DcStorePageVo;
import com.haizhi.graph.dc.store.service.ConnectTestService;
import com.haizhi.graph.dc.store.service.StoreManageService;
import com.haizhi.graph.sys.auth.model.po.QSysUserPo;
import com.haizhi.graph.sys.file.model.po.QSysDictPo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import com.haizhi.graph.sys.file.service.SysDictService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by chengangxiong on 2019/03/26
 */
@Service
public class StoreManageServiceImpl extends JpaBase implements StoreManageService {

    private static final GLog LOG = LogFactory.getLogger(StoreManageServiceImpl.class);

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private ConnectTestService connectTestService;

    @Override
    public PageResponse findPage(DcStoreQo qo) {
        try {
            QDcStorePo storeTable = QDcStorePo.dcStorePo;
            QSysUserPo userTable = QSysUserPo.sysUserPo;
            QDcEnvPo envTable = QDcEnvPo.dcEnvPo;
            QSysDictPo dictTable = QSysDictPo.sysDictPo;

            BooleanBuilder builder = new BooleanBuilder();
            if (StringUtils.isNotEmpty(qo.getName())) {
                builder.and(storeTable.name.like(JQL.likeWrap(qo.getName())));
            }
            if (Objects.nonNull(qo.getStoreType())) {
                builder.and(storeTable.type.eq(qo.getStoreType()));
            }

            PageQo pageQo = qo.getPage();
            JPAQuery<Tuple> query = jpa
                    .select(storeTable, envTable.name, dictTable.value, userTable.name).from(storeTable)
                    .leftJoin(envTable).on(storeTable.envId.eq(envTable.id))
                    .leftJoin(userTable).on(userTable.id.stringValue().eq(storeTable.createdById))
                    .leftJoin(dictTable).on(storeTable.versionDictId.eq(dictTable.id))
                    .where(builder)
                    .orderBy(storeTable.updatedDt.desc())
                    .offset(pageQo.getPageSize() * (pageQo.getPageNo() - 1))
                    .limit(pageQo.getPageSize());
            QueryResults<Tuple> queryResult = query.fetchResults();
            List<DcStorePageVo> voList2 = queryResult.getResults().stream().map(new Function<Tuple, DcStorePageVo>() {
                @Override
                public DcStorePageVo apply(Tuple tuple) {
                    DcStorePo storePo = tuple.get(0, DcStorePo.class);
                    String envName = tuple.get(1, String.class);
                    String version = tuple.get(2, String.class);
                    String createByName = tuple.get(3, String.class);
                    return new DcStorePageVo(storePo, createByName, envName, version);
                }
            }).collect(Collectors.toList());

            return PageResponse.success(voList2, queryResult.getTotal(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.STORE_PAGE_ERROR, e);
        }
    }

    @Override
    public Response<List<SysDictPo>> supportedVersion(StoreType storeType) {
        List<SysDictPo> res = sysDictService.findStoreSupportedVersion(storeType.name());
        if (Objects.isNull(res) || res.size() == 0){
            throw new RuntimeException("no supported version found");
        }
        return Response.success(res);
    }

    @Override
    public Response<String> testConnect(DcStoreSuo dcStoreSuo) {
        return connectTestService.process(dcStoreSuo);
    }

    @Override
    public Response<String> testConnect(String exportUrl) {
        return connectTestService.gpExportUrl(exportUrl);
    }

    @Override
    public Response<String> autoGenUrl(DcStoreAutoUrlQo dcStoreAutoUrlQo) {
        return connectTestService.autoGenUrl(dcStoreAutoUrlQo.getEnvId(), dcStoreAutoUrlQo.getStoreType());
    }
}
