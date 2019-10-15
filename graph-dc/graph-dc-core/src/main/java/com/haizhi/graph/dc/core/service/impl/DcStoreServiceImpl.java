package com.haizhi.graph.dc.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.core.login.utils.ShiroUtils;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.CoreStatus;
import com.haizhi.graph.dc.core.dao.DcStoreDao;
import com.haizhi.graph.dc.core.dao.DcStoreParamDao;
import com.haizhi.graph.dc.core.model.po.DcStoreParamPo;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import com.haizhi.graph.dc.core.model.po.QDcGraphStorePo;
import com.haizhi.graph.dc.core.model.po.QDcStorePo;
import com.haizhi.graph.dc.core.model.qo.DcNameCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.core.model.suo.DcStoreParamSuo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.core.model.vo.*;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import com.haizhi.graph.dc.core.service.DcStoreService;
import com.haizhi.graph.sys.file.dao.SysDictDao;
import com.haizhi.graph.sys.file.model.po.QSysDictPo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by zhouduqing on 2018/12/24.
 */
@Slf4j
@Service
public class DcStoreServiceImpl extends JpaBase implements DcStoreService {

    private static final GLog LOG = LogFactory.getLogger(DcStoreServiceImpl.class);

    @Autowired
    private DcStoreDao dcStoreDao;

    @Autowired
    private DcStoreParamDao dcStoreParamDao;

    @Autowired
    private DcStorePubService dcStorePubService;

    @Autowired
    private SysDictDao sysDictDao;

    @Override
    public PageResponse findPage(DcStoreQo qo) {
        try {
            QDcStorePo storeTable = QDcStorePo.dcStorePo;
            BooleanBuilder builder = new BooleanBuilder();
            if (StringUtils.isNotEmpty(qo.getName())) {
                builder.and(storeTable.name.like(JQL.likeWrap(qo.getName())));
            }
            if (Objects.nonNull(qo.getStoreType())) {
                builder.and(storeTable.type.eq(qo.getStoreType()));
            }
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.UPDATED_DT_DESC);

            Page<DcStorePo> page = dcStoreDao.findAll(builder, pageRequest);
            List<DcStorePageVo> voList = page.getContent().stream().map(new Function<DcStorePo, DcStorePageVo>() {
                @Override
                public DcStorePageVo apply(DcStorePo storePo) {
                    return new DcStorePageVo(storePo);
                }
            }).collect(Collectors.toList());
            return PageResponse.success(voList, page.getTotalElements(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.STORE_PAGE_ERROR, e);
        }
    }

    @Override
    public Response<List<String>> findStoreTypeList() {
        List<String> storeTypeList = EnumUtils.getEnumList(StoreType.class).stream().map(Enum::name).collect(Collectors.toList());
        storeTypeList.remove(StoreType.UNKNOWN.name());
        return Response.success(storeTypeList);
    }

    @Override
    public Response<List<DcStorePageVo>> find(DcStoreQo qo) {
        QDcStorePo table = QDcStorePo.dcStorePo;
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(qo.getStoreType())) {
            builder = builder.and(table.type.eq(qo.getStoreType()));
        }
        List<DcStorePo> result = jpa.select(table).from(table).where(builder).fetch();
        List<DcStorePageVo> voList = result.stream().map(storePo -> new DcStorePageVo(storePo)).collect(Collectors.toList());
        return Response.success(voList);
    }

    @Override
    public Response<List<DcStoreSelectorVo>> findAll(@NonNull StoreType storeType) {
        QDcStorePo table = QDcStorePo.dcStorePo;
        QSysDictPo dictTable = QSysDictPo.sysDictPo;
        JPAQuery<Tuple> query = jpa.select(table, dictTable.value)
                .from(table).leftJoin(dictTable).on(table.versionDictId.eq(dictTable.id))
                .where(table.type.eq(storeType));
        List<Tuple> queryResult = query.fetch();
        List<DcStoreSelectorVo> voList = queryResult.stream().map(tuple -> {
            DcStorePo dcstorePo = tuple.get(0, DcStorePo.class);
            String version = tuple.get(1, String.class);
            return new DcStoreSelectorVo(dcstorePo, version);
        }).collect(Collectors.toList());
        return Response.success(voList);
    }

    @Override
    public Response<DcStoreVo> findById(Long id) {
        DcStorePo po = dcStoreDao.findOne(id);
        DcStoreVo vo = null;
        if (po != null) {
            Long storeId = po.getId();
            SysDictPo dictPo = sysDictDao.findOne(po.getVersionDictId());
            List<DcStoreParamPo> dcStoreParamPoList = dcStoreParamDao.findByStoreId(storeId);
            List<DcStoreParamVo> dcStoreParamVoList = dcStoreParamPoList.stream().map(DcStoreParamVo::new).collect(Collectors.toList());
            vo = new DcStoreVo(po, dictPo == null ? "" : dictPo.getValue());
            vo.setDcStoreParamList(dcStoreParamVoList);
        }
        return Response.success(vo);
    }

    @Override
    public DcStorePo findByNameAndType(String name, StoreType storeType) {
        return dcStoreDao.findByNameAndType(name, storeType);
    }

    @Override
    public List<DcStorePo> findByGraph(String graph) {
        QDcStorePo storePo = QDcStorePo.dcStorePo;
        QDcGraphStorePo graphStorePo = QDcGraphStorePo.dcGraphStorePo;
        JPAQuery<DcStorePo> jpaQuery = jpa.select(storePo).from(storePo)
                .innerJoin(graphStorePo).on(storePo.id.eq(graphStorePo.storeId))
                .where(graphStorePo.graph.eq(graph));
        List<DcStorePo> dcStorePos = jpaQuery.fetch();
        return dcStorePos;
    }

    @Override
    public List<DcStoreVo> findVoByGraph(String graph) {
        QDcStorePo storePo = QDcStorePo.dcStorePo;
        QDcGraphStorePo graphStorePo = QDcGraphStorePo.dcGraphStorePo;
        QSysDictPo dictPo = QSysDictPo.sysDictPo;
        JPAQuery<Tuple> jpaQuery = jpa.select(storePo, dictPo.value).from(storePo)
                .innerJoin(graphStorePo).on(storePo.id.eq(graphStorePo.storeId))
                .leftJoin(dictPo).on(storePo.versionDictId.eq(dictPo.id))
                .where(graphStorePo.graph.eq(graph));
        List<Tuple> queryResult = jpaQuery.fetch();
        List<DcStoreVo> voList = queryResult.stream().map(tuple -> {
            DcStorePo dcstorePo = tuple.get(0, DcStorePo.class);
            String version = tuple.get(1, String.class);
            return new DcStoreVo(dcstorePo, version);
        }).collect(Collectors.toList());
        return voList;
    }

    @Override
    @Transactional
    public Response saveOrUpdate(DcStoreSuo suo) {
        LOG.info("DcStoreSuo: {0}", JSON.toJSONString(suo, true));
        checkStoreNameUnique(suo.getName(), suo.getId());
        try {
            DcStorePo dcStorePo = new DcStorePo(suo);
            setUpdateBy(dcStorePo);
            dcStorePo = dcStoreDao.save(dcStorePo);
            List<DcStoreParamSuo> paramSuoList = suo.getDcStoreParamList();
            if (Objects.nonNull(paramSuoList) && !paramSuoList.isEmpty()) {
                Long storeId = dcStorePo.getId();
                dcStoreParamDao.deleteByStoreId(storeId);
                paramSuoList.forEach(dcStoreParamSuo -> {
                    DcStoreParamPo paramPo = new DcStoreParamPo(storeId, dcStoreParamSuo);
                    dcStoreParamDao.save(paramPo);
                });
            }
            dcStorePubService.publishByStoreId(dcStorePo.getId());
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.STORE_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public Response<DcNameCheckVo> check(DcNameCheckQO dcNameCheckQO) {
        LOG.info("dcNameCheckQO: {0}", JSON.toJSONString(dcNameCheckQO, true));
        DcNameCheckVo checkVo = new DcNameCheckVo();
        try {
            checkStoreNameUnique(dcNameCheckQO.getName(), dcNameCheckQO.getId());
        } catch (Exception e) {
            checkVo.setCheckResult(false);
            checkVo.setCheckMsg(e.getMessage());
        }
        return Response.success(checkVo);
    }

    @Override
    public Response delete(Long id) {
        try {
            dcStoreDao.delete(id);
            dcStorePubService.publishByStoreId(id);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.STORE_DELETE_ERROR, e);
        }
        return Response.success();
    }

    private void checkStoreNameUnique(String store, Long id) {
        DcStorePo sameStore;
        if (Objects.isNull(id)) {
            sameStore = dcStoreDao.findByName(store);
        } else {
            sameStore = dcStoreDao.findByNameAndIdIsNot(store, id);
        }
        if (Objects.nonNull(sameStore)) {
            throw new UnexpectedStatusException(CoreStatus.STORE_NAME_EXISTS);
        }
    }
}