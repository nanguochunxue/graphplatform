package com.haizhi.graph.sys.file.service.impl;

import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.sys.file.dao.SysDictDao;
import com.haizhi.graph.sys.file.model.po.QSysDictPo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import com.haizhi.graph.sys.file.service.SysDictService;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by chengangxiong on 2019/03/26
 */
@Service
public class SysDictServiceImpl extends JpaBase implements SysDictService {

    @Autowired
    private SysDictDao sysDictDao;

    @Override
    public List<SysDictPo> findStoreSupportedVersion(String storeType) {
        return findDict("STORE_TYPE", storeType, "VERSION");
    }

    @Override
    public List<SysDictPo> findEnvSupportedVersion() {
        List<SysDictPo> envVersion = findDict("ENV_VERSION");
        return envVersion;
    }

    private List<SysDictPo> findDict(@NotNull String key) {
        return sysDictDao.findAllByKey(key);
    }

    @Override
    public SysDictPo findById(Long id) {
        return sysDictDao.findOne(id);
    }

    private List<SysDictPo> findDict(String parentKey, String parentValue, String subKey){
        QSysDictPo storeTable = new QSysDictPo("store");
        QSysDictPo versionTable = new QSysDictPo("version");
        JPAQuery<SysDictPo> query = jpa.select(versionTable)
                .from(versionTable).where(versionTable.key.eq(subKey))
                .leftJoin(storeTable).on(versionTable.parentId.eq(storeTable.id))
                .where(storeTable.key.eq(parentKey)
                        .and(storeTable.value.eq(parentValue))
                ).orderBy(versionTable.value.desc());
        return query.fetch();
    }
}
