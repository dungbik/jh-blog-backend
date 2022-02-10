package com.yoonleeverse.blog.route.file.resolver;

import com.yoonleeverse.blog.route.file.domain.File;
import com.yoonleeverse.blog.route.file.service.FileService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileMutationResolver implements GraphQLMutationResolver {

    private final FileService uploadService;

    //    @PreAuthorize("isAuthenticated()")
    public File upload(Part part) {
        log.debug("Upload: " + part.getSubmittedFileName());
        return uploadService.upload(part);
    }
}
