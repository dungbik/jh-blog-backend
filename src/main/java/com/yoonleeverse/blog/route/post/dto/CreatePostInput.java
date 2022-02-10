package com.yoonleeverse.blog.route.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostInput {

    private String title;
    private String content;
    private List<String> tags;
    @JsonProperty("thumbnailId")
    private Long thumbnailId;
}
