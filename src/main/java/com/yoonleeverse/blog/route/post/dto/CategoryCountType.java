package com.yoonleeverse.blog.route.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryCountType {

    private Long id;

    private String name;

    private Integer count;
}
