package com.haizhi.graph.sys.file.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/26
 */
@Repository
public interface SysDictDao extends JpaRepo<SysDictPo> {

    List<SysDictPo> findAllByKey(String key);
}
