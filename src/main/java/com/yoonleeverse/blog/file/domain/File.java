package com.yoonleeverse.blog.file.domain;

import com.yoonleeverse.blog.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private String originalName;

    private String realName;

    @ManyToOne()
    @JoinColumn(name = "post_id")
    private Post post;
}
