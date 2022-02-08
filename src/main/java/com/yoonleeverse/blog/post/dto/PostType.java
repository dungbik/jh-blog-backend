package com.yoonleeverse.blog.post.dto;

import com.yoonleeverse.blog.post.domain.Post;
import com.yoonleeverse.blog.post.domain.PostTag;
import com.yoonleeverse.blog.post.domain.Tag;
import com.yoonleeverse.blog.file.domain.File;
import com.yoonleeverse.blog.user.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostType {

    private Long postId;

    private String title;

    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private User author;

    private List<String> tags;

    private File thumbnail;

    public static PostType makePostType(Post post) {
        PostType pt = PostType.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .author(post.getAuthor())
                .tags(new ArrayList<>())
                .build();

        if (post.getPostTags() != null)
            pt.addTags(post.getPostTags());
        return pt;
    }

    public void addTags(Set<PostTag> postTags) {
        tags.addAll(postTags.stream().map(PostTag::getTag).map(Tag::getName).collect(Collectors.toList()));
    }
}
