package com.yoonleeverse.blog.post.domain;

import com.yoonleeverse.blog.file.domain.File;
import com.yoonleeverse.blog.user.domain.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long postId;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @EqualsAndHashCode.Include
    private String content;

    @CreatedDate
    @Column(updatable = false)
    @EqualsAndHashCode.Include
    private LocalDateTime createdDate;

    @LastModifiedDate
    @EqualsAndHashCode.Include
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Include
    private User author;

    @Setter
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PostTag> postTags;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<File> thumbnail;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<File> files;

}
