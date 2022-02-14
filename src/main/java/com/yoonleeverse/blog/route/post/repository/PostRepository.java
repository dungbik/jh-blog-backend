package com.yoonleeverse.blog.route.post.repository;

import com.yoonleeverse.blog.route.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.postTags JOIN FETCH p.files LEFT JOIN FETCH p.thumbnail WHERE p.postId = (:id) ORDER BY p.createdDate DESC")
    Optional<Post> findById(@Param("id") Long id);

    @Query("SELECT p.postId FROM Post p ORDER BY p.createdDate DESC")
    Page<Long> getAllId(Pageable page);

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.postTags JOIN FETCH p.files LEFT JOIN FETCH p.thumbnail WHERE p.postId IN (:ids)")
    List<Post> getAllByIds(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.postTags JOIN FETCH p.files LEFT JOIN FETCH p.thumbnail WHERE p.postId IN (:ids) ORDER BY p.createdDate DESC")
    List<Post> findAllById(@Param("ids") List<Long> ids);
}
