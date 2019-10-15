package com.haizhi.graph.tag.core.dao;

import com.haizhi.graph.tag.core.domain.TagCategory;
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
public interface TagCategoryDAO extends CrudRepository<TagCategory, Long> {

    @Query(value = "SELECT * from tag_category as t where t.graph=:graph and t.enabled_flag='Y' order by t.id", nativeQuery = true)
    List<TagCategory> findAll(@Param("graph") String graph);
}
