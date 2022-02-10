package com.yoonleeverse.blog.file.storage;

import com.yoonleeverse.blog.file.domain.File;
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
import java.util.Optional;
import java.util.UUID;

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

            String realName = UUID.randomUUID().toString() + getExtension(part.getSubmittedFileName());

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
    public Resource loadAsResource(File file) {
        try {
            Path loadFile = load(file.getRealName());
            Resource resource = new UrlResource(loadFile.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                throw new Exception("Could not read file: " + loadFile);

        } catch (Exception e) {
            throw new RuntimeException("Could not read file - id : " + file.getFileId(), e);
        }
    }

    private Optional<String> getExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".")));
    }
}
