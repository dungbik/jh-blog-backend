package com.yoonleeverse.blog.route.file.repository;

import com.yoonleeverse.blog.route.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByRealName(String realName);
}
