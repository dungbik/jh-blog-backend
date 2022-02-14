package com.yoonleeverse.blog.route.post.service;

import com.yoonleeverse.blog.route.file.domain.File;
import com.yoonleeverse.blog.route.file.repository.FileRepository;
import com.yoonleeverse.blog.route.post.domain.*;
import com.yoonleeverse.blog.route.post.dto.*;
import com.yoonleeverse.blog.route.post.repository.*;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final UserService userService;
    private final FileRepository fileRepository;
    private final CategoryRepository categoryRepository;
    private final TagCategoryRepository tagCategoryRepository;

    @Transactional
    public List<PostType> getAllPost(Long category, List<Long> tagList, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());

        if (category == null) {
            if (tagList.size() == 0) {
                List<Long> ids = postRepository.getAllId(pageRequest).getContent();

                return postRepository.getAllByIds(ids).stream()
                        .map(PostType::makePostType)
                        .collect(Collectors.toList());
            }

            List<Long> postIds = postTagRepository.findAllPostIdByTagId(tagList, pageRequest).getContent();
            if (postIds.size() == 0)
                return null;

            List<Post> posts = postRepository.findAllById(postIds);
            return posts.stream()
                    .map(PostType::makePostType)
                    .collect(Collectors.toList());
        } else {
            List<Long> tags = tagCategoryRepository.getAllTagIdByCategoryId(category);
            if (tags.size() == 0)
                return null;

            List<Long> postIds = postTagRepository.findAllPostIdByTagId(tags, pageRequest).getContent();
            if (postIds.size() == 0)
                return null;

            List<Post> posts = postRepository.findAllById(postIds);
            return posts.stream()
                    .map(PostType::makePostType)
                    .collect(Collectors.toList());
        }
    }

    public Optional<PostType> getPost(Long postId) {
        return Optional.ofNullable(postRepository.findById(postId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(PostType::makePostType);
    }

    private Tag getTagByName(String tagName) {
        Tag tag = tagRepository.findByName(tagName).orElse(null);
        if (tag == null) {
            tag = Tag.builder().name(tagName).build();
            tagRepository.save(tag);
        }
        return tag;
    }

    @Transactional
    public PostType createPost(CreatePostInput input) {
        User user = userService.getCurrentUser();
        File thumbnail = fileRepository.findById(input.getThumbnail()).orElse(null);

        if (thumbnail == null)
            throw new RuntimeException("thumbnail is null");

        Post post = Post.builder()
                .author(user)
                .title(input.getTitle())
                .content(input.getContent())
                .build();

        thumbnail.setPost(post);
        fileRepository.save(thumbnail);
        postRepository.save(post);

        Set<PostTag> postTags = input.getTags().stream()
                .map(this::getTagByName)
                .map(tag -> {
                    PostTag postTag = PostTag.builder().post(post).tag(tag).build();
                    return postTagRepository.save(postTag);
                })
                .collect(Collectors.toSet());

        Set<File> thumnail = Stream.of(thumbnail)
                .collect(Collectors.toSet());

        post.setPostTags(postTags);
        post.setThumbnail(thumnail);
        return PostType.makePostType(post);
    }

    @Transactional
    public CategoryType createCategory(CreateCategoryInput input) {

        if (categoryRepository.findByName(input.getName()).isPresent())
            return null;

        Category category = Category.builder()
                .name(input.getName())
                .build();
        categoryRepository.save(category);

        Set<TagCategory> postTags = input.getTags().stream()
                .filter(tagId -> tagRepository.findById(tagId).isPresent())
                .map(tagRepository::findById)
                .map((tag) -> {
                    TagCategory tagCategory = TagCategory.builder()
                            .category(category)
                            .tag(tag.get())
                            .build();
                    return tagCategoryRepository.save(tagCategory);
                })
                .collect(Collectors.toSet());

        category.setTags(postTags);
        return CategoryType.makeCategoryType(category);
    }

    public List<CategoryInfoType> getCategoryInfo(List<Long> ids) {

        List<String> categoryNames = categoryRepository.getAllNameById(ids);

        List<List<PostCountType>> infos = ids.stream()
                .filter(id -> categoryRepository.findById(id).isPresent())
                .map(tagCategoryRepository::getAllTagIdByCategoryId)
                .map(postTagRepository::getAllNameAndCountByTagId)
                .map(interfaces -> interfaces.stream()
                        .map(i -> PostCountType.builder().name(i.getName())
                                .count(i.getCount())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

        List<CategoryInfoType> result = new ArrayList<>();
        for (int i = 0; i < categoryNames.size(); i++) {
            int total = 0;
            for (PostCountType pct : infos.get(i)) {
                total += pct.getCount();
            }
            result.add(CategoryInfoType.builder()
                    .category(new PostCountType(categoryNames.get(i), total))
                    .tags(infos.get(i))
                    .build());
        }

        return result;
    }

}
