package com.haizhi.graph.tag.core.dao;

import com.haizhi.graph.tag.core.domain.TagDependency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
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
public interface TagDependencyDAO extends CrudRepository<TagDependency, Long> {

    @Query(value = "SELECT * from tag_dependency as t where t.graph=:graphName order by t.from_tag_id", nativeQuery = true)
    List<TagDependency> getTagDependencies(@Param("graphName") String graphName);

    @Query(value = "SELECT * from tag_dependency as t where t.graph=:graphName and t.to_tag_id =:toTagId", nativeQuery = true)
    List<TagDependency> getTagDependencies(@Param("graphName") String graphName, @Param("toTagId") Long toTagId);

    @Query(value = "SELECT * from tag_dependency as t where t.graph=:graphName and t.from_tag_id =:fromTagId", nativeQuery = true)
    List<TagDependency> getTagDependenciesByFromTagId(@Param("graphName") String graphName, @Param("fromTagId") Long fromTagId);
}
