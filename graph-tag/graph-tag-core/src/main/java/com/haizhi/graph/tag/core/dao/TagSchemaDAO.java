package com.haizhi.graph.tag.core.dao;

import com.haizhi.graph.tag.core.domain.TagSchema;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/3/2.
 */
@Transactional
@Repository
public interface TagSchemaDAO extends CrudRepository<TagSchema, Long> {

    @Query(value = "SELECT * from tag_schema as t where t.graph=:graph order by t.id", nativeQuery = true)
    List<TagSchema> getTagSchemas(@Param("graph") String graph);

    @Query(value = "SELECT * from tag_schema as t where t.graph=:graph and t.tag_id=:tagId", nativeQuery = true)
    List<TagSchema> getTagSchemas(@Param("graph") String graph, @Param("tagId") Long tagId);
}
