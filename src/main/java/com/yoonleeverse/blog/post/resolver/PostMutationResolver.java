package com.yoonleeverse.blog.post.resolver;

import com.yoonleeverse.blog.post.domain.Category;
import com.yoonleeverse.blog.post.dto.CategoryType;
import com.yoonleeverse.blog.post.dto.CreateCategoryInput;
import com.yoonleeverse.blog.post.dto.CreatePostInput;
import com.yoonleeverse.blog.post.dto.PostType;
import com.yoonleeverse.blog.post.service.PostService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMutationResolver implements GraphQLMutationResolver {

    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    public PostType createPost(CreatePostInput input) {
        return postService.createPost(input);
    }

    //@PreAuthorize("isAuthenticated()")
    public CategoryType createCategory(CreateCategoryInput input) {
        return postService.createCategory(input);
    }

}
