package com.yoonleeverse.blog.post.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long tagId;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<PostTag> postTags;

}
