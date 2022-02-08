package com.yoonleeverse.blog.post.resolver;

import com.yoonleeverse.blog.post.domain.Post;
import com.yoonleeverse.blog.post.dto.CategoryInfoType;
import com.yoonleeverse.blog.post.dto.PostType;
import com.yoonleeverse.blog.post.service.PostService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostQueryResolver implements GraphQLQueryResolver {

    private final PostService postService;

    public List<PostType> getAllPost(List<Long> tags, int page, int size) {
        return postService.getAllPost(tags, page, size);
    }

    public Optional<PostType> getPost(Long postId) {
        return postService.getPost(postId);
    }

    public List<CategoryInfoType> getCategoryInfo(List<Long> ids) {
        return postService.getCategoryInfo(ids);
    }
}
