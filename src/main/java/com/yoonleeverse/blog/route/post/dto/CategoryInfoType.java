package com.yoonleeverse.blog.route.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryInfoType {

    PostCountType category;

    List<PostCountType> tags;
}
