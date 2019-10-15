package com.haizhi.graph.tag.core.dao;

import com.haizhi.graph.tag.core.domain.TagParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by chengmo on 2018/3/2.
 */
@Transactional
@Repository
public interface TagParameterDAO extends JpaRepository<TagParameter, Long>, JpaSpecificationExecutor<TagParameter> {

    @Query(value = "SELECT * from tag_parameter as t where t.graph=:graph", nativeQuery = true)
    List<TagParameter> getTagParameters(@Param("graph") String graph);

    @Query(value = "SELECT * from tag_parameter as t where t.graph=:graph and t.id in(:ids)", nativeQuery = true)
    List<TagParameter> getTagParameters(@Param("graph") String graph, @Param("ids") Set<Long> ids);

    @Query(value = "SELECT * from tag_parameter as t where t.graph=:graph and t.type in(:types)", nativeQuery = true)
    List<TagParameter> getTagParametersByTypes(@Param("graph") String graph, @Param("types") Set<String> types);

    @Query(value = "SELECT * from tag_parameter as t where t.graph=:graph and t.reference=:reference", nativeQuery = true)
    TagParameter getTagParameterByReference(@Param("graph") String graph, @Param("reference") String reference);
}
