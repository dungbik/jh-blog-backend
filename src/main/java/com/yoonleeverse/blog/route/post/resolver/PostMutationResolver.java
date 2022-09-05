package com.yoonleeverse.blog.route.post.resolver;

import com.sun.xml.bind.v2.schemagen.xmlschema.SimpleType;
import com.yoonleeverse.blog.route.common.dto.BasicType;
import com.yoonleeverse.blog.route.post.dto.CategoryType;
import com.yoonleeverse.blog.route.post.dto.CreateCategoryInput;
import com.yoonleeverse.blog.route.post.dto.CreatePostInput;
import com.yoonleeverse.blog.route.post.dto.PostType;
import com.yoonleeverse.blog.route.post.service.PostService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@RequiredArgsConstructor
public class PostMutationResolver implements GraphQLMutationResolver {

    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    public PostType createPost(@Valid CreatePostInput input) {
        return postService.createPost(input);
    }

    @PreAuthorize("isAuthenticated()")
    public CategoryType createCategory(CreateCategoryInput input) {
        return postService.createCategory(input);
    }

    @PreAuthorize("isAuthenticated")
    public BasicType deletePost(Long postId) {
        return postService.deletePost(postId);
    }


}
