package com.yoonleeverse.blog.route.post.service;

import com.yoonleeverse.blog.route.common.dto.BasicType;
import com.yoonleeverse.blog.route.file.domain.File;
import com.yoonleeverse.blog.route.file.repository.FileRepository;
import com.yoonleeverse.blog.route.file.storage.StorageService;
import com.yoonleeverse.blog.route.post.domain.*;
import com.yoonleeverse.blog.route.post.dto.*;
import com.yoonleeverse.blog.route.post.repository.*;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.service.UserService;
import jdk.jfr.StackTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private final StorageService storageService;

    @Transactional
    public List<PostType> getAllPost(Long category, List<Long> tagList, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());

        if (category == null) {
            if (tagList.size() == 0) {
                List<Long> ids = postRepository.getAllId(pageRequest).getContent();
                if (ids.size() == 0)
                    return null;

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
            TagCategory tagCategory = TagCategory.builder()
                    .category(categoryRepository.findByName("미등록 태그").get())
                    .tag(tag)
                    .build();
            tagCategoryRepository.save(tagCategory);
        }
        return tag;
    }

    @Transactional
    public PostType createPost(CreatePostInput input) {
        User user = userService.getCurrentUser();
        File thumbnail = null;
        if (input.getThumbnail() != null)
            thumbnail = fileRepository.findById(input.getThumbnail()).orElse(null);

        Post post = Post.builder()
                .author(user)
                .title(input.getTitle())
                .content(input.getContent())
                .build();

        if (thumbnail != null) {
            thumbnail.setPost(post);
            fileRepository.save(thumbnail);
        }

        postRepository.save(post);

        Set<PostTag> postTags = input.getTags().stream()
                .map(this::getTagByName)
                .map(tag -> {
                    PostTag postTag = PostTag.builder().post(post).tag(tag).build();
                    return postTagRepository.save(postTag);
                })
                .collect(Collectors.toSet());

        post.setPostTags(postTags);
        if (thumbnail != null)
            post.setThumbnail(thumbnail);

        Set<File> files = input.getAttachments().stream()
                .filter(id -> fileRepository.findById(id).isPresent())
                .map(fileRepository::findById)
                .map(Optional::get)
                .map(file -> {
                    file.setPost(post);
                    fileRepository.save(file);

                    return file;
                })
                .collect(Collectors.toSet());
        post.setFiles(files);

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
                .map(Optional::get)
                .map((tag) -> {
                    tagCategoryRepository.getByNames("미등록 태그", tag.getName())
                            .ifPresent(tagCategoryRepository::delete);

                    TagCategory tagCategory = TagCategory.builder()
                            .category(category)
                            .tag(tag)
                            .build();
                    return tagCategoryRepository.save(tagCategory);
                })
                .collect(Collectors.toSet());

        category.setTags(postTags);
        return CategoryType.makeCategoryType(category);
    }

    public List<CategoryInfoType> getCategoryInfo(List<Long> ids) {
        List<Category> categories;

        if (ids.size() == 0)
            categories = categoryRepository.getAll();
        else
            categories = categoryRepository.getAllById(ids);

        List<List<Long>> tagList = new ArrayList<>();

        List<List<TagCountType>> infos = categories.stream()
                .map(category -> {
                    List<Long> tags = tagCategoryRepository.getAllTagIdByCategoryId(category.getCategoryId());
                    tagList.add(tags);
                    return tags;
                })
                .map(postTagRepository::getAllNameAndCountByTagId)
                .map(interfaces -> interfaces.stream()
                        .map(i -> TagCountType.builder().name(i.getName())
                                .id(i.getId())
                                .count(i.getCount())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

        List<CategoryInfoType> result = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            int total = postTagRepository.countPostByTagId(tagList.get(i));

            result.add(CategoryInfoType.builder()
                    .category(new CategoryCountType(categories.get(i).getCategoryId(), categories.get(i).getName(), total))
                    .tags(infos.get(i))
                    .build());
        }

        return result;
    }

    @Transactional
    public BasicType deletePost(Long postId) {

        BasicType res = new BasicType();

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

            if (post.getThumbnail() != null) {
                storageService.delete(post.getThumbnail().getRealName());
            }

            if (!CollectionUtils.isEmpty(post.getFiles())) {
                post.getFiles().stream()
                        .forEach(file -> storageService.delete(file.getRealName()));
            }

            postTagRepository.deleteByPost(post);
            fileRepository.deleteByPost(post);
            postRepository.deleteById(postId);

            res.setSuccess(true);
        } catch (Exception e) {
            res.setSuccess(false);
            log.error(e.getMessage());
        }

        return res;
    }

    @Transactional
    public PostType updatePost(UpdatePostInput input) {

        Post post = postRepository.findById(input.getPostId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        post.updatePost(input.getTitle(), input.getContent());

        postTagRepository.deleteByPost(post);
        Set<PostTag> postTags = input.getTags().stream()
                .map(this::getTagByName)
                .map(tag -> {
                    PostTag postTag = PostTag.builder().post(post).tag(tag).build();
                    return postTagRepository.save(postTag);
                })
                .collect(Collectors.toSet());

        post.setPostTags(postTags);

        if ((post.getThumbnail() == null && input.getThumbnail() != null)
                || post.getThumbnail().getFileId() != input.getThumbnail()) {
            File thumbnail = fileRepository.findById(input.getThumbnail())
                    .orElse(null);

            if (post.getThumbnail() == null) {
                storageService.delete(post.getThumbnail().getRealName());
                fileRepository.deleteById(post.getThumbnail().getFileId());
            }

            if (thumbnail != null) {
                thumbnail.setPost(post);
                fileRepository.save(thumbnail);
                post.setThumbnail(thumbnail);
            }
        }

        // todo 첨부 파일 update 도 해야됨

        return PostType.makePostType(post);
    }
}
