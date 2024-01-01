package com.vicheak.coreapp.api.file.web;

import com.vicheak.coreapp.api.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDto uploadSingle(@RequestPart MultipartFile file) {
        return fileService.uploadSingle(file);
    }

    @GetMapping(value = "/download/{name}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadByName(@PathVariable("name") String name) {
        Resource resource = fileService.downloadByName(name);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .body(resource);
    }

}
