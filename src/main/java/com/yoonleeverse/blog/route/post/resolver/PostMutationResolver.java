package com.yoonleeverse.blog.route.post.resolver;

import com.sun.xml.bind.v2.schemagen.xmlschema.SimpleType;
import com.yoonleeverse.blog.route.common.dto.BasicType;
import com.yoonleeverse.blog.route.post.dto.*;
import com.yoonleeverse.blog.route.post.service.PostService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PostMutationResolver implements GraphQLMutationResolver {

    private final PostService postService;

    public PostType createPost(@Valid CreatePostInput input) {
        return postService.createPost(input);
    }

    public CategoryType createCategory(CreateCategoryInput input) {
        return postService.createCategory(input);
    }

    public BasicType deletePost(Long postId) {
        return postService.deletePost(postId);
    }

    public PostType updatePost(@Valid UpdatePostInput input) {
        return postService.updatePost(input);
    }

}
