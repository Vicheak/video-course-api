package com.vicheak.coreapp.api.file;

import com.vicheak.coreapp.api.file.web.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.server-path}")
    private String serverPath;

    @Value("${file.base-uri}")
    private String baseUri;

    @Value("${file.download-uri}")
    private String downloadUri;

    @Override
    public FileDto uploadSingle(MultipartFile file) {
        return this.save(file);
    }

    @Override
    public FileDto uploadSingleRestrictImage(MultipartFile file) {
        //check a valid image extension
        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (extension.equals("png") ||
                extension.equals("jpg") ||
                extension.equals("webp") ||
                extension.equals("jpeg"))
            return save(file);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Unsupported image extension!");
    }

    @Override
    public Resource downloadByName(String name) {
        Path path = Paths.get(serverPath + name);

        if (Files.exists(path))
            //start loading the resource by file name
            return UrlResource.from(path.toUri());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Resource has not been found!");
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastDotIndex + 1);
    }

    private FileDto save(MultipartFile file) {
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "File is empty! Cannot upload the file!");

        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));

        //create unique file name by random uuid
        String name = UUID.randomUUID() + "." + extension;

        String uri = baseUri + name;
        Long size = file.getSize();

        //create file path (absolute path)
        Path path = Paths.get(serverPath + name);

        try {
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileDto.builder()
                .name(name)
                .uri(uri)
                .downloadUri(downloadUri + name)
                .size(size)
                .extension(extension)
                .build();
    }

}
