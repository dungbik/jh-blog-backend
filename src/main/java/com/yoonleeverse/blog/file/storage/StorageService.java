package com.yoonleeverse.blog.file.storage;

import org.springframework.core.io.Resource;

import javax.servlet.http.Part;
import java.nio.file.Path;

public interface StorageService {

    void init();

    String store(Part part);

    public Path load(String filename);

    public Resource loadAsResource(String filename);
}
