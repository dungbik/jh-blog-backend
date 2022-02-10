package com.yoonleeverse.blog.route.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateCategoryInput {

    private String name;
    private List<Long> tags;
}
