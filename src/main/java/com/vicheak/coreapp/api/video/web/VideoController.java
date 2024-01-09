package com.vicheak.coreapp.api.video.web;

import com.vicheak.coreapp.api.course.web.CourseDto;
import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.video.VideoService;
import com.vicheak.coreapp.base.BaseApi;
import com.vicheak.coreapp.pagination.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    public BaseApi<?> loadAllVideos() {

        List<VideoDto> videoDtoList = videoService.loadAllVideos();

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("All videos loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(videoDtoList)
                .build();
    }

    @GetMapping("/{uuid}")
    public BaseApi<?> loadVideoByUuid(@PathVariable String uuid) {

        VideoDto videoDto = videoService.loadVideoByUuid(uuid);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Video with uuid, %s loaded successfully!".formatted(uuid))
                .timestamp(LocalDateTime.now())
                .payload(videoDto)
                .build();
    }

    @GetMapping("/paginate")
    public BaseApi<?> loadPaginatedVideos(@RequestParam(required = false) Map<String, String> requestMap) {

        PageDto pageDto = videoService.loadPaginatedVideos(requestMap);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Videos loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(pageDto)
                .build();
    }

    @GetMapping("/search")
    public BaseApi<?> searchCourses(@RequestParam(required = false) Map<String, String> requestMap) {

        List<VideoDto> videoDtoList = videoService.searchVideos(requestMap);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Videos loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(videoDtoList)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createNewVideo(@RequestBody @Valid TransactionVideoDto transactionVideoDto) {
        videoService.createNewVideo(transactionVideoDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{uuid}")
    public void updateVideoByUuid(@PathVariable String uuid,
                                  @RequestBody TransactionVideoDto transactionVideoDto) {
        videoService.updateVideoByUuid(uuid, transactionVideoDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public void deleteVideoByUuid(@PathVariable String uuid) {
        videoService.deleteVideoByUuid(uuid);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/uploadImage/{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDto uploadVideoImageByUuid(@PathVariable String uuid,
                                          @RequestPart MultipartFile file) {
        return videoService.uploadVideoImageByUuid(uuid, file);
    }

    @GetMapping("/me")
    public BaseApi<?> loadVideosByAuthenticatedAuthor() {

        List<VideoDto> videoDtoList = videoService.loadVideosByAuthenticatedAuthor();

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Videos loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(videoDtoList)
                .build();
    }

}
