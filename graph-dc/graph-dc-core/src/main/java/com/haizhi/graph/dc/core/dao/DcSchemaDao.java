package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/8/16.
 */
@Transactional
@Repository
public interface DcSchemaDao extends JpaRepo<DcSchemaPo>, CrudRepository<DcSchemaPo, Long> {

    DcSchemaPo findByGraphAndSchema(String graph, String schema);

    DcSchemaPo findByIdIsNotAndGraphAndSchema(Long id, String graph, String schema);

    List<DcSchemaPo> findByGraph(String graph);

//    @Query("SELECT SUM(use_search) AS EsCount,SUM(use_hbase) AS HbaseCount , SUM(use_gdb) AS GdbCount FROM DcSchemaPo AS dc WHERE dc.graph=:graph")
//    Map<String, Integer> countByGraph(@Param("graph") String graph);
}
