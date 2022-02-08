package com.yoonleeverse.blog.file.repository;

import com.yoonleeverse.blog.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
