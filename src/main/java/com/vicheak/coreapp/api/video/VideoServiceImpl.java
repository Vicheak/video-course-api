package com.vicheak.coreapp.api.video;

import com.vicheak.coreapp.api.course.Course;
import com.vicheak.coreapp.api.course.CourseRepository;
import com.vicheak.coreapp.api.course.CourseServiceImpl;
import com.vicheak.coreapp.api.course.Course_;
import com.vicheak.coreapp.api.file.FileService;
import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.video.web.TransactionVideoDto;
import com.vicheak.coreapp.api.video.web.VideoDto;
import com.vicheak.coreapp.pagination.LoadPageable;
import com.vicheak.coreapp.pagination.PageDto;
import com.vicheak.coreapp.spec.VideoFilter;
import com.vicheak.coreapp.spec.VideoSpec;
import com.vicheak.coreapp.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final CourseServiceImpl courseService;
    private final CourseRepository courseRepository;
    private final FileService fileService;

    @Override
    public List<VideoDto> loadAllVideos() {
        return videoMapper.fromVideoToVideoDto(videoRepository.findAll());
    }

    @Override
    public VideoDto loadVideoByUuid(String uuid) {
        Video video = videoRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Video with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        return videoMapper.fromVideoToVideoDto(video);
    }

    @Override
    public PageDto loadPaginatedVideos(Map<String, String> requestMap) {
        //load the pagination
        Pageable pageable = LoadPageable.loadPageable(requestMap);

        Page<Video> pages = videoRepository.findAll(pageable);

        //cast content from page of video to page of video dto
        List<VideoDto> contents = videoMapper.fromVideoToVideoDto(pages.getContent());

        return new PageDto(contents, pages);
    }

    @Override
    public List<VideoDto> searchVideos(Map<String, String> requestMap) {
        //extract data from request map
        VideoFilter.VideoFilterBuilder videoFilterBuilder = VideoFilter.builder();

        if (requestMap.containsKey(Video_.TITLE))
            videoFilterBuilder.title(requestMap.get(Video_.TITLE));

        if (requestMap.containsKey("fromDate"))
            videoFilterBuilder.fromDate(LocalDate.parse(requestMap.get("fromDate")));

        if (requestMap.containsKey("toDate"))
            videoFilterBuilder.toDate(LocalDate.parse(requestMap.get("toDate")));

        //set default direction
        String direction = "asc";
        if (requestMap.containsKey(SortUtil.DIRECTION.getLabel()))
            direction = requestMap.get(SortUtil.DIRECTION.getLabel());

        //set default property
        String field = "";
        if (requestMap.containsKey(SortUtil.FIELD.getLabel()))
            field = requestMap.get(SortUtil.FIELD.getLabel());

        //build jpa spec
        List<Video> videos = videoRepository.findAll(
                VideoSpec.builder()
                        .videoFilter(videoFilterBuilder.build())
                        .build(),
                Sort.by(
                        direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        field.isEmpty() ? Course_.TITLE : field
                )
        );

        return videoMapper.fromVideoToVideoDto(videos);
    }

    @Transactional
    @Override
    public void createNewVideo(TransactionVideoDto transactionVideoDto) {
        //check if course does not exist
        Course course = courseRepository.findById(transactionVideoDto.courseId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Course does not exist in the system!")
                );

        //check the security context holder
        courseService.checkSecurityOperationWithoutAdmin(course);

        //map from dto to entity
        Video newVideo = videoMapper.fromTransactionVideoDtoToVideo(transactionVideoDto);
        newVideo.setUuid(UUID.randomUUID().toString());

        //save new video to the database
        videoRepository.save(newVideo);
    }

    @Transactional
    @Override
    public void updateVideoByUuid(String uuid, TransactionVideoDto transactionVideoDto) {
        //load video by uuid
        Video video = videoRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Video with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check the security context holder
        courseService.checkSecurityOperationWithoutAdmin(video.getCourse());

        //check if course does not exist
        if (Objects.nonNull(transactionVideoDto.courseId()))
            if (!courseRepository.existsById(transactionVideoDto.courseId()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course does not exist, please check!");

        //map from dto to entity
        videoMapper.fromTransactionVideoDtoToVideo(video, transactionVideoDto);

        if (Objects.nonNull(transactionVideoDto.courseId())) {
            Course newCourse = courseRepository.findById(transactionVideoDto.courseId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Course has not been found in the system!")
                    );

            //check the security context holder
            courseService.checkSecurityOperationWithoutAdmin(newCourse);

            video.setCourse(newCourse);
        }

        //save video to the database
        videoRepository.save(video);
    }

    @Transactional
    @Override
    public void deleteVideoByUuid(String uuid) {
        Video video = videoRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Video with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check the security context holder
        courseService.checkSecurityOperationWithoutAdmin(video.getCourse());

        videoRepository.delete(video);
    }

    @Transactional
    @Override
    public FileDto uploadVideoImageByUuid(String uuid, MultipartFile file) {
        Video video = videoRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Video with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check the security context holder
        courseService.checkSecurityOperationWithoutAdmin(video.getCourse());

        FileDto fileDto = fileService.uploadSingleRestrictImage(file);

        //set video image
        video.setImageCover(fileDto.name());

        videoRepository.save(video);

        return fileDto;
    }

}
