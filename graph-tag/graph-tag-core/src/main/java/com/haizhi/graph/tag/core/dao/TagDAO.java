package com.haizhi.graph.tag.core.dao;

import com.haizhi.graph.tag.core.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/3/2.
 */
@Transactional
@Repository
public interface TagDAO extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    @Query(value = "SELECT * from tag as t where t.graph=:graph and t.enabled_flag='Y' order by t.id", nativeQuery = true)
    List<Tag> findAll(@Param("graph") String graph);

    @Query(value = "SELECT * from tag as t where t.graph=:graph order by t.id", nativeQuery = true)
    List<Tag> findAllTags(@Param("graph") String graph);

    @Query(value = "SELECT * from tag as t where t.graph=:graph and t.tag_status=:tagStatus and t.enabled_flag='Y' order by t.id", nativeQuery = true)
    List<Tag> findByTagStatus(@Param("graph") String graph, @Param("tagStatus") String tagStatus);

    @Query(value = "SELECT distinct t.graph from tag as t where t.enabled_flag='Y'", nativeQuery = true)
    List<String> findGraphNames();

    /*@Query(value = "SELECT * from tag as t where t.graph=:graphName ORDER BY ?#{#pageable}", nativeQuery = true)
    Page<Tag> getTagsPage(@Param("graphName") String graphName, Pageable pageable);*/
}
