package com.yoonleeverse.blog.post.dto;

import com.yoonleeverse.blog.post.domain.Category;
import com.yoonleeverse.blog.post.domain.Tag;
import com.yoonleeverse.blog.post.domain.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryType {

    private Long categoryId;

    private String name;

    private List<String> tags;

    public static CategoryType makeCategoryType(Category category) {
        CategoryType categoryType = CategoryType.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .tags(new ArrayList<>())
                .build();

        if (category.getTags() != null)
            categoryType.addTags(category.getTags());

        return categoryType;
    }

    public void addTags(Set<TagCategory> tags) {
        this.tags.addAll(tags.stream()
                .map(TagCategory::getTag)
                .map(Tag::getName)
                .collect(Collectors.toList()));
    }
}
