package com.yoonleeverse.blog.file.service;

import com.yoonleeverse.blog.file.domain.File;
import com.yoonleeverse.blog.file.repository.FileRepository;
import com.yoonleeverse.blog.file.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;

@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageService storageService;
    private final FileRepository fileRepository;

    public File upload(Part part) {
        String realName = storageService.store(part);
        File savedFile = File.builder()
                .originalName(part.getSubmittedFileName())
                .realName(realName)
                .build();
        fileRepository.save(savedFile);
        return savedFile;
    }
}
