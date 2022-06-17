package com.github.jvanheesch.document;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RequestMapping(path = "/documents")
@RestController
public class DocumentController {
    private final DocumentRepository documentRepository;

    @PostMapping
    public Document upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return documentRepository.save(file.getName(), file.getBytes());
    }
}
