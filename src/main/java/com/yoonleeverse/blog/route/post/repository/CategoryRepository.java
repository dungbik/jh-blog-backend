package com.yoonleeverse.blog.route.post.repository;

import com.yoonleeverse.blog.route.post.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT DISTINCT c FROM Category c JOIN FETCH c.tags WHERE c.categoryId = (:id)")
    Optional<Category> findById(@Param("id") Long id);

    Optional<Category> findByName(String name);

    @Query("SELECT c.name FROM Category c WHERE c.categoryId IN (:ids)")
    List<String> getAllNameById(@Param("ids") List<Long> ids);
}
