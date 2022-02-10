package com.yoonleeverse.blog.route.post.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long categoryId;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<TagCategory> tags;

}
