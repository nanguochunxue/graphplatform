package com.haizhi.graph.sys.file.service;

import com.haizhi.graph.sys.file.model.po.SysFilePo;

/**
 * Created by chengmo on 2019/1/31.
 */
public interface SysFileService {

    SysFilePo saveOrUpdate(SysFilePo po);

    Iterable<SysFilePo> findByIds(Long[] ids);

    SysFilePo findById(Long id);
}
