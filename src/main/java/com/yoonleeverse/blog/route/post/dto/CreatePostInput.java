package com.yoonleeverse.blog.route.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreatePostInput {

    private String title;
    private String content;
    private List<String> tags;
    private Long thumbnail;
}
