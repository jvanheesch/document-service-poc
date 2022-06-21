package com.github.jvanheesch.document;

import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@AllArgsConstructor
@RequestMapping(path = "/documents")
@RestController
public class DocumentController {
    private final DocumentRepository documentRepository;
    private final DocumentGenerator documentGenerator;

    @PostMapping
    public Document upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return documentRepository.save(file.getName(), file.getBytes());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return ResponseEntity
                .ok()
                .headers(h -> h.setContentDisposition(ContentDisposition.builder("attachment")
                        // could be replaced with document.name
                        .filename("content.txt")
                        .build())
                )
                .body(documentRepository.download(id));
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generate() {
        return documentGenerator.generateDocument()
                .map(data -> ResponseEntity
                        .ok()
                        .headers(h -> h.setContentDisposition(ContentDisposition.builder("attachment")
                                // could be replaced with document.name
                                .filename("content.txt")
                                .build())
                        )
                        .body(data))
                .block();
    }
}
