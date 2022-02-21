package com.yoonleeverse.blog.route.post.repository;

import com.yoonleeverse.blog.route.post.domain.PostTag;
import com.yoonleeverse.blog.route.post.dto.PostCountInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Query("SELECT pt.post.postId FROM PostTag pt WHERE pt.tag.tagId IN (:ids)")
    Page<Long> findAllPostIdByTagId(@Param("ids") List<Long> ids, Pageable page);

    @Query("SELECT COUNT(DISTINCT pt.post) FROM PostTag pt WHERE pt.tag.tagId IN (:ids)")
    Integer countPostByTagId(@Param("ids") List<Long> ids);

    @Query("SELECT pt.tag.tagId AS id, pt.tag.name AS name, COUNT(pt.post) AS count FROM PostTag pt WHERE pt.tag.tagId IN (:ids) GROUP BY pt.id")
    List<PostCountInterface> getAllNameAndCountByTagId(@Param("ids") List<Long> ids);


}
