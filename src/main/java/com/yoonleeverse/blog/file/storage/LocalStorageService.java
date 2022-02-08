package com.yoonleeverse.blog.file.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private Path rootPath;
    private final StorageProperties storageProperties;

    @Override
    @PostConstruct
    public void init() {
        try {
            rootPath = Paths.get(storageProperties.getPath());
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    @Override
    public String store(Part part) {
        try {
            if (part == null)
                throw new Exception("Part is null");

            String realName = part.getSubmittedFileName();
            String filePath = storageProperties.getPath() + realName;
            part.write(filePath);

            return realName;
        } catch (Exception e) {
            throw new RuntimeException("Could not save the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Path load(String filename) {
        return rootPath.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                throw new Exception("Could not read file: " + filename);

        } catch (Exception e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
}
