package com.yoonleeverse.blog.post.repository;

import com.yoonleeverse.blog.post.domain.Post;
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

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.postTags WHERE p.postId = (:id) ORDER BY p.createdDate DESC")
    Optional<Post> findById(@Param("id") Long id);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.postTags ORDER BY p.createdDate DESC",
            countQuery = "SELECT COUNT(p) FROM Post p JOIN p.postTags")
    Page<Post> findAll(Pageable page);

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.postTags WHERE p.postId IN (:ids) ORDER BY p.createdDate DESC")
    List<Post> findAllById(@Param("ids") List<Long> ids);
}
