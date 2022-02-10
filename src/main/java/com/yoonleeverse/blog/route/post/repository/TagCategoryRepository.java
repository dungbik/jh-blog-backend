package com.yoonleeverse.blog.route.post.repository;

import com.yoonleeverse.blog.route.post.domain.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {

    @Query("SELECT tc.tag.tagId FROM TagCategory tc WHERE tc.category.categoryId = (:id)")
    List<Long> getAllTagIdByCategoryId(@Param("id") Long id);
}
