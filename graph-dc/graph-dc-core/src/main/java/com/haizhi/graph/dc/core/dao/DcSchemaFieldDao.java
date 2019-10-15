package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/8/16.
 */
@Transactional
@Repository
public interface DcSchemaFieldDao extends JpaRepo<DcSchemaFieldPo> {

    void deleteByGraphAndSchema(String graph, String schema);

    List<DcSchemaFieldPo> findByGraphAndSchemaAndIsMain(String graph, String schema, boolean isMain);

    List<DcSchemaFieldPo> findByGraphAndSchema(String graph, String schema);

    DcSchemaFieldPo findByGraphAndSchemaAndField(String graph, String schema, String field);

    DcSchemaFieldPo findByIdIsNotAndGraphAndSchemaAndField(Long id, String graph, String schema, String field);
}
