package com.haizhi.graph.sys.file.service.impl;

import com.haizhi.graph.sys.file.dao.SysFileDao;
import com.haizhi.graph.sys.file.model.po.QSysFilePo;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import com.haizhi.graph.sys.file.service.SysFileService;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chengangxiong on 2019/01/31
 */
@Service
public class SysFileServiceImpl implements SysFileService {

    @Autowired
    private SysFileDao sysFileDao;

    @Override
    public SysFilePo saveOrUpdate(SysFilePo po) {
        return sysFileDao.save(po);
    }

    @Override
    public Iterable<SysFilePo> findByIds(Long[] ids) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QSysFilePo.sysFilePo.id.in(ids));
        return sysFileDao.findAll(booleanBuilder);
    }

    @Override
    public SysFilePo findById(Long id) {
        return sysFileDao.findOne(id);
    }
}
