package com.haizhi.graph.dc.core.service.impl;

import com.google.common.collect.Lists;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.constant.StoreStatus;
import com.haizhi.graph.dc.core.dao.DcStoreDao;
import com.haizhi.graph.dc.core.model.po.QDcEnvPo;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import com.haizhi.graph.dc.core.service.DcEnvFileService;
import com.haizhi.graph.dc.core.service.DcEnvService;
import com.haizhi.graph.dc.core.dao.DcEnvDao;
import com.haizhi.graph.dc.core.dao.DcEnvFileDao;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import com.haizhi.graph.dc.core.model.suo.DcEnvSuo;
import com.haizhi.graph.dc.core.model.vo.DcEnvDetailVo;
import com.haizhi.graph.dc.core.model.vo.DcEnvFileVo;
import com.haizhi.graph.dc.core.model.vo.DcEnvVo;
import com.haizhi.graph.sys.file.model.po.QSysDictPo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import com.haizhi.graph.sys.file.service.SysDictService;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Service
public class DcEnvServiceImpl extends JpaBase implements DcEnvService {

    private static final GLog LOG = LogFactory.getLogger(DcEnvServiceImpl.class);

    @Autowired
    private DcEnvDao dcEnvDao;

    @Autowired
    private DcEnvFileDao dcEnvFileDao;

    @Autowired
    private DcEnvFileService dcEnvFileService;

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private DcStoreDao dcStoreDao;

    @Autowired
    private DcStorePubService dcStorePubService;

    @Override
    public Response<DcEnvVo> findAll() {
        try {
            QDcEnvPo envTable = QDcEnvPo.dcEnvPo;
            QSysDictPo versionTable = QSysDictPo.sysDictPo;
            JPAQuery<Tuple> query = jpa.select(envTable, versionTable)
                    .from(envTable).leftJoin(versionTable)
                    .on(envTable.versionDictId.eq(versionTable.id))
                    .orderBy(envTable.createdDt.desc());
            List<DcEnvVo> dcEnvVoList = query.fetch().stream().map(tuple -> {
                DcEnvPo envPo = tuple.get(0, DcEnvPo.class);
                SysDictPo dictPo = tuple.get(1, SysDictPo.class);
                DcEnvVo vo = new DcEnvVo(envPo, dictPo);
                return vo;
            }).collect(Collectors.toList());
            return Response.success(dcEnvVoList);
        }catch (Exception e){
            LOG.error(e);
            throw new UnexpectedStatusException(StoreStatus.ENV_FIND_FAIL);
        }
    }

    @Override
    @Transactional
    public Response saveOrUpdate(DcEnvSuo dcEnvSuo) {
        DcEnvPo po = new DcEnvPo(dcEnvSuo);
        try {
            Long[] fileIds = dcEnvSuo.getFileIds();
            List<Long> currentFileIds = Lists.newArrayList(fileIds == null ? new Long[0] : fileIds);
            if (po.getId() != null){ // save
                List<DcEnvFilePo> fileList = dcEnvFileDao.findAllByEnvId(po.getId());
                List<Long> oldFileIds = fileList.stream().map(BasePo::getId).collect(Collectors.toList());
                oldFileIds.removeAll(currentFileIds);
                dcEnvFileService.deleteById(po.getId(), oldFileIds);
            }
            po = dcEnvDao.save(po);
            dcEnvFileService.updateEnvId(po.getId(), currentFileIds);
            dcStorePubService.publishByEnvId(po.getId());
        }catch (Exception e){
            LOG.error(e);
            throw new UnexpectedStatusException(StoreStatus.ENV_SAVE_UPDATE_FAIL);
        }
        return Response.success();
    }

    @Override
    public DcEnvPo findOne(Long id) {
        return dcEnvDao.findOne(id);
    }

    @Override
    public Response<DcEnvDetailVo> findById(Long id) {
        DcEnvPo dcEnvPo = dcEnvDao.findOne(id);
        List<DcEnvFilePo> envFileList = dcEnvFileDao.findAllByEnvId(id);
        List<DcEnvFileVo> envFileVoList = null;
        SysDictPo dictPo = sysDictService.findById(dcEnvPo.getVersionDictId());
        if(Objects.nonNull(envFileList)){
            envFileVoList = envFileList.stream().map(DcEnvFileVo::new).collect(Collectors.toList());
        }
        return Response.success(new DcEnvDetailVo(dcEnvPo, dictPo, envFileVoList));
    }

    @Override
    public Response<List<SysDictPo>> findEnvVersion() {
        List<SysDictPo> sysDictPoList = sysDictService.findEnvSupportedVersion();
        if (Objects.isNull(sysDictPoList) | sysDictPoList.size() != 1){
            throw new RuntimeException("error env version config,please check table dc_dict, dict key is ENV_VERION,and should only one");
        }
        return Response.success(sysDictPoList);
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        if (dcStoreDao.findByEnvId(id).size() != 0){
            return Response.error(StoreStatus.ENV_CANNOT_DELETE.getDesc());
        }
        try {
            dcEnvDao.delete(id);
            dcEnvFileDao.deleteByEnvId(id);
            dcStorePubService.publishByEnvId(id);
        }catch (Exception e){
            LOG.error(e);
            throw new UnexpectedStatusException(StoreStatus.ENV_DELETE_FAIL);
        }
        return Response.success();
    }
}
