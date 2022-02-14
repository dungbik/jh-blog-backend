package com.yoonleeverse.blog.db;

import com.yoonleeverse.blog.route.file.domain.File;
import com.yoonleeverse.blog.route.file.repository.FileRepository;
import com.yoonleeverse.blog.route.post.domain.Category;
import com.yoonleeverse.blog.route.post.repository.CategoryRepository;
import com.yoonleeverse.blog.route.user.domain.Authority;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DBInit implements InitializingBean {

    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!userService.findUser("test@test.com").isPresent()) {
            User user = userService.save(User.builder()
                    .email("test@test.com")
                    .password(passwordEncoder.encode("test"))
                    .build());
            userService.addAuthority(user.getUserId(), Authority.ROLE_ADMIN);
        }

        if (!categoryRepository.findByName("미등록 태그").isPresent()) {
            Category category = Category.builder()
                    .name("미등록 태그")
                    .build();
            categoryRepository.save(category);
        }

        if (!fileRepository.findByRealName("default.jpeg").isPresent()) {
            File file = File.builder()
                    .originalName("default.jpeg")
                    .realName("default.jpeg")
                    .build();
            fileRepository.save(file);
        }



    }
}
