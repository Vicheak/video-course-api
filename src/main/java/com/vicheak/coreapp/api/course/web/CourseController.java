package com.vicheak.coreapp.api.course.web;

import com.vicheak.coreapp.api.course.CourseService;
import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.video.web.VideoDto;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public BaseApi<?> loadAllCourses() {

        List<CourseDto> courseDtoList = courseService.loadAllCourses();

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("All courses loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(courseDtoList)
                .build();
    }

    @GetMapping("/{uuid}")
    public BaseApi<?> loadCourseByUuid(@PathVariable String uuid) {

        CourseDto courseDto = courseService.loadCourseByUuid(uuid);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Course with uuid, %s loaded successfully!".formatted(uuid))
                .timestamp(LocalDateTime.now())
                .payload(courseDto)
                .build();
    }

    @GetMapping("/paginate")
    public BaseApi<?> loadPaginatedCourses(@RequestParam(required = false) Map<String, String> requestMap) {

        PageDto pageDto = courseService.loadPaginatedCourses(requestMap);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Courses loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(pageDto)
                .build();
    }

    @GetMapping("/search")
    public BaseApi<?> searchCourses(@RequestParam(required = false) Map<String, Object> requestMap) {

        List<CourseDto> courseDtoList = courseService.searchCourses(requestMap);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Courses loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(courseDtoList)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BaseApi<?> createNewCourse(@RequestBody @Valid TransactionCourseDto transactionCourseDto) {

        CourseDto newCourseDto = courseService.createNewCourse(transactionCourseDto);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.CREATED.value())
                .message("A course has been created successfully!")
                .timestamp(LocalDateTime.now())
                .payload(newCourseDto)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{uuid}")
    public BaseApi<?> updateCourseByUuid(@PathVariable String uuid,
                                         @RequestBody TransactionCourseDto transactionCourseDto) {

        CourseDto updatedCourseDto = courseService.updateCourseByUuid(uuid, transactionCourseDto);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("A course has been updated successfully!")
                .timestamp(LocalDateTime.now())
                .payload(updatedCourseDto)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{uuid}")
    public BaseApi<?> deleteCourseByUuid(@PathVariable String uuid) {

        courseService.deleteCourseByUuid(uuid);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("A course has been deleted successfully!")
                .timestamp(LocalDateTime.now())
                .payload(Map.of("message", "Payload has no content!"))
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/uploadImage/{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDto uploadCourseImageByUuid(@PathVariable String uuid,
                                           @RequestPart MultipartFile file) {
        return courseService.uploadCourseImageByUuid(uuid, file);
    }

    @GetMapping("/{uuid}/videos")
    public BaseApi<?> loadVideosByCourseUuid(@PathVariable String uuid) {

        List<VideoDto> videoDtoList = courseService.loadVideosByCourseUuid(uuid);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Videos loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(videoDtoList)
                .build();
    }

    @GetMapping("/me")
    public BaseApi<?> loadCoursesByAuthenticatedAuthor(Authentication authentication) {

        List<CourseDto> courseDtoList = courseService.loadCoursesByAuthenticatedAuthor(authentication);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Courses loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(courseDtoList)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/like")
    public BaseApi<?> likeCourseByUser(@RequestBody @Valid LikeDto likeDto,
                                       Authentication authentication) {

        courseService.likeCourseByUser(likeDto, authentication);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.CREATED.value())
                .message("A course has been interacted!")
                .timestamp(LocalDateTime.now())
                .payload("Operated successfully")
                .build();
    }

}
