package com.yoonleeverse.blog.route.post.dto;

import com.yoonleeverse.blog.route.post.domain.Post;
import com.yoonleeverse.blog.route.post.domain.PostTag;
import com.yoonleeverse.blog.route.post.domain.Tag;
import com.yoonleeverse.blog.route.file.domain.File;
import com.yoonleeverse.blog.route.user.domain.User;

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

    private String thumbnail;

    private List<String> files;

    public static PostType makePostType(Post post) {
        PostType pt = PostType.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .author(post.getAuthor())
                .tags(new ArrayList<>())
                .files(new ArrayList<>())
                .thumbnail(post.getThumbnail() == null
                        ? "default.jpeg"
                        : post.getThumbnail().getRealName())
                .build();

        if (post.getPostTags() != null)
            pt.addTags(post.getPostTags());

        if (post.getFiles() != null)
            pt.addFiles(post.getFiles());

        return pt;
    }

    public void addTags(Set<PostTag> postTags) {
        tags.addAll(postTags.stream().map(PostTag::getTag).map(Tag::getName).collect(Collectors.toList()));
    }

    public void addFiles(Set<File> files) {
        this.files.addAll(files.stream().map(File::getRealName).collect(Collectors.toList()));
    }
}
