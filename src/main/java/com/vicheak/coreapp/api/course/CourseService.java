package com.vicheak.coreapp.api.course;

import com.vicheak.coreapp.api.course.web.CourseDto;
import com.vicheak.coreapp.api.course.web.TransactionCourseDto;
import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.video.web.VideoDto;
import com.vicheak.coreapp.pagination.PageDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CourseService {

    /**
     * This method is used to load all course resources in the system
     *
     * @return List<CourseDto>
     */
    List<CourseDto> loadAllCourses();

    /**
     * This method is used to load specific course resource by uuid
     *
     * @param uuid is the path parameter from client
     * @return CourseDto
     */
    CourseDto loadCourseByUuid(String uuid);

    /**
     * This method is used to load paginated course resources in the system
     *
     * @param requestMap is the request from client
     * @return PageDto
     */
    PageDto loadPaginatedCourses(Map<String, String> requestMap);

    /**
     * This method is used to search courses via requested criteria
     *
     * @param requestMap is the request from client
     * @return List<CourseDto>
     */
    List<CourseDto> searchCourses(Map<String, Object> requestMap);

    /**
     * This method is used to create new course resource into the system
     *
     * @param transactionCourseDto is the request from client
     * @return CourseDto
     */
    CourseDto createNewCourse(TransactionCourseDto transactionCourseDto);

    /**
     * This method is used to update specific course resource by uuid
     *
     * @param uuid                 is the path parameter from client
     * @param transactionCourseDto is the request from client
     * @return CourseDto
     */
    CourseDto updateCourseByUuid(String uuid, TransactionCourseDto transactionCourseDto);

    /**
     * This method is used to delete specific course resource by uuid
     *
     * @param uuid is the path parameter from client
     */
    void deleteCourseByUuid(String uuid);

    /**
     * This method is used to upload single course image by course uuid
     *
     * @param uuid is the path parameter from client
     * @param file is the request part from client
     * @return FileDto
     */
    FileDto uploadCourseImageByUuid(String uuid, MultipartFile file);

    /**
     * This method is used to load video resources by course uuid
     * @param uuid is the path parameter from client
     * @return List<VideoDto>
     */
    List<VideoDto> loadVideosByCourseUuid(String uuid);

    /**
     * This method is used to load courses by authenticated author
     * @param authentication is the request from client
     * @return List<CourseDto>
     */
    List<CourseDto> loadCoursesByAuthenticatedAuthor(Authentication authentication);

}
