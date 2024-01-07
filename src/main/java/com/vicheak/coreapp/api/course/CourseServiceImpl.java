package com.vicheak.coreapp.api.course;

import com.vicheak.coreapp.api.category.Category;
import com.vicheak.coreapp.api.category.CategoryRepository;
import com.vicheak.coreapp.api.course.web.CourseDto;
import com.vicheak.coreapp.api.course.web.TransactionCourseDto;
import com.vicheak.coreapp.api.file.FileService;
import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.user.User;
import com.vicheak.coreapp.api.video.VideoMapper;
import com.vicheak.coreapp.api.video.VideoRepository;
import com.vicheak.coreapp.api.video.web.VideoDto;
import com.vicheak.coreapp.pagination.LoadPageable;
import com.vicheak.coreapp.pagination.PageDto;
import com.vicheak.coreapp.security.CustomUserDetails;
import com.vicheak.coreapp.spec.CourseFilter;
import com.vicheak.coreapp.spec.CourseSpec;
import com.vicheak.coreapp.util.SortUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final EntityManager entityManager;

    @Override
    public List<CourseDto> loadAllCourses() {
        return courseMapper.fromCourseToCourseDto(courseRepository.findAll());
    }

    @Override
    public CourseDto loadCourseByUuid(String uuid) {
        Course course = courseRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Course with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        return courseMapper.fromCourseToCourseDto(course);
    }

    @Override
    public PageDto loadPaginatedCourses(Map<String, String> requestMap) {
        //load the pagination
        Pageable pageable = LoadPageable.loadPageable(requestMap);

        Page<Course> pages = courseRepository.findAll(pageable);

        //cast content from page of course to page of course dto
        List<CourseDto> contents = courseMapper.fromCourseToCourseDto(pages.getContent());

        return new PageDto(contents, pages);
    }

    @Override
    public List<CourseDto> searchCourses(Map<String, Object> requestMap) {
        //extract the data from request map
        CourseFilter.CourseFilterBuilder courseFilterBuilder = CourseFilter.builder();

        if (requestMap.containsKey(Course_.TITLE))
            courseFilterBuilder.title(requestMap.get(Course_.TITLE).toString());

        if (requestMap.containsKey(Course_.DURATION_IN_HOUR))
            courseFilterBuilder.durationInHour(Integer.parseInt((String) requestMap.get(Course_.DURATION_IN_HOUR)));

        if (requestMap.containsKey(Course_.COST))
            courseFilterBuilder.cost(new BigDecimal((String) requestMap.get(Course_.COST)));

        //set default direction
        String direction = "asc";
        if (requestMap.containsKey(SortUtil.DIRECTION.getLabel()))
            direction = requestMap.get(SortUtil.DIRECTION.getLabel()).toString();

        //set default property
        String field = "";
        if (requestMap.containsKey(SortUtil.FIELD.getLabel()))
            field = requestMap.get(SortUtil.FIELD.getLabel()).toString();

        //build jpa spec
        List<Course> courses = courseRepository.findAll(
                CourseSpec.builder()
                        .courseFilter(courseFilterBuilder.build())
                        .build(),
                Sort.by(
                        direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        field.isEmpty() ? Course_.TITLE : field
                )
        );

        return courseMapper.fromCourseToCourseDto(courses);
    }

    @Transactional
    @Override
    public CourseDto createNewCourse(TransactionCourseDto transactionCourseDto) {
        //check if course's title already exists
        if (courseRepository.existsByTitleIgnoreCase(transactionCourseDto.title()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Course's title conflicts resource in the system!");

        //check and validate category id from dto
        if (!categoryRepository.existsById(transactionCourseDto.categoryId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Category ID does not exist in the system!");

        //map from dto to entity
        Course course = courseMapper.fromTransactionCourseDtoToCourse(transactionCourseDto);
        course.setUuid(UUID.randomUUID().toString());
        course.setNumberOfView(0L);
        course.setNumberOfLike(0L);

        course = courseRepository.save(course);

        //refresh the entity via entity manager
        entityManager.refresh(course);

        return courseMapper.fromCourseToCourseDto(course);
    }

    @Transactional
    @Override
    public CourseDto updateCourseByUuid(String uuid, TransactionCourseDto transactionCourseDto) {
        //load course by uuid
        Course course = courseRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Course with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check the security operation before give the user permission to process
        checkSecurityOperation(course);

        //check if course's title already exists (except the previous title)
        if (Objects.nonNull(transactionCourseDto.title()))
            if (!transactionCourseDto.title().equalsIgnoreCase(course.getTitle()) &&
                    courseRepository.existsByTitleIgnoreCase(transactionCourseDto.title()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Course's title conflicts resource in the system!");

        //check and validate category id from dto
        if (Objects.nonNull(transactionCourseDto.categoryId()))
            if (!categoryRepository.existsById(transactionCourseDto.categoryId()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category ID does not exist in the system!");

        //map from dto to entity
        courseMapper.fromTransactionCourseDtoToCourse(course, transactionCourseDto);

        //alter instance category of course
        if (Objects.nonNull(transactionCourseDto.categoryId())) {
            //load the existing category
            Category newCategory = categoryRepository.findById(transactionCourseDto.categoryId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Category ID does not exist in the system!")
                    );
            course.setCategory(newCategory);
        }

        //save the updated course
        courseRepository.save(course);

        return courseMapper.fromCourseToCourseDto(course);
    }

    @Transactional
    @Override
    public void deleteCourseByUuid(String uuid) {
        Course course = courseRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Course with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check the security operation before give the user permission to process
        checkSecurityOperation(course);

        courseRepository.delete(course);
    }

    @Transactional
    @Override
    public FileDto uploadCourseImageByUuid(String uuid, MultipartFile file) {
        Course course = courseRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Course with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check the security operation before give the user permission to process
        checkSecurityOperation(course);

        FileDto fileDto = fileService.uploadSingleRestrictImage(file);

        //set course image
        course.setImage(fileDto.name());

        courseRepository.save(course);

        return fileDto;
    }

    @Override
    public List<VideoDto> loadVideosByCourseUuid(String uuid) {
        return videoMapper.fromVideoToVideoDto(videoRepository.findByCourseUuid(uuid));
    }

    @Override
    public List<CourseDto> loadCoursesByAuthenticatedAuthor(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User authenticated = customUserDetails.getUser();
        return courseMapper.fromCourseToCourseDto(courseRepository.findByUser(authenticated));
    }

    public void checkSecurityOperation(Course course) {
        //check the security context holder
        //if the user is ADMIN, allow the operation
        //if the user is AUTHOR, allow the operation, but only with the specified courses
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User authenticatedUser = customUserDetails.getUser();
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");

        if (customUserDetails.getAuthorities().contains(adminAuthority))
            return;

        if (!course.getUser().getUuid().equals(authenticatedUser.getUuid()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Permission denied!");
    }

    public void checkSecurityOperationWithoutAdmin(Course course) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User authenticatedUser = customUserDetails.getUser();

        if (!course.getUser().getUuid().equals(authenticatedUser.getUuid()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Permission denied!");
    }

}
