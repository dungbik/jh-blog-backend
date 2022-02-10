package com.yoonleeverse.blog.route.file.controller;

import com.yoonleeverse.blog.route.file.repository.FileRepository;
import com.yoonleeverse.blog.route.file.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileRepository fileRepository;
    private final StorageService storageService;

    @GetMapping("/serve/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        return fileRepository.findByRealName(filename)
                .map(file -> ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalName() + "\"")
                        .body(storageService.loadAsResource(file)))
                .orElse(ResponseEntity.notFound().build());
    }
}
