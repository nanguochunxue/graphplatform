package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Repository
public interface DcEnvFileDao extends JpaRepo<DcEnvFilePo> {

    void deleteByEnvId(Long envId);

    List<DcEnvFilePo> findAllByEnvId(Long envId);
}
