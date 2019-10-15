package com.haizhi.graph.sys.file.service;

import com.haizhi.graph.sys.file.model.po.SysDictPo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/26
 */
public interface SysDictService {

    List<SysDictPo> findStoreSupportedVersion(String storeType);

    List<SysDictPo> findEnvSupportedVersion();

    SysDictPo findById(Long id);
}
