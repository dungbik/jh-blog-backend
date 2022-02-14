package com.yoonleeverse.blog.route.post.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreatePostInput {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private List<String> tags;

    @NotNull
    private Long thumbnail;

    @NotNull
    private List<Long> attachments;
}
