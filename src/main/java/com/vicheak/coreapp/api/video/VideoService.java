package com.vicheak.coreapp.api.video;

import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.video.web.TransactionVideoDto;
import com.vicheak.coreapp.api.video.web.VideoDto;
import com.vicheak.coreapp.pagination.PageDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VideoService {

    /**
     * This method is used to load all video resources from the system
     * @return List<VideoDto>
     */
    List<VideoDto> loadAllVideos();

    /**
     * This method is used to load specific video resource by uuid
     * @param uuid is the path parameter from client
     * @return VideoDto
     */
    VideoDto loadVideoByUuid(String uuid);

    /**
     * This method is used to load videos by pagination (_page=?, _limit=?)
     * @param requestMap is the request from client
     * @return PageDto
     */
    PageDto loadPaginatedVideos(Map<String, String> requestMap);

    /**
     * This method is used to search video resources followed by the criteria
     * @param requestMap is the request from client
     * @return List<VideoDto>
     */
    List<VideoDto> searchVideos(Map<String, String> requestMap);

    /**
     * This method is used to create new video resource into the system
     * @param transactionVideoDto is the request from client
     */
    void createNewVideo(TransactionVideoDto transactionVideoDto);

    /**
     * This method is used to update specific video resource by uuid
     * @param uuid is the path parameter from client
     * @param transactionVideoDto is the request from client
     */
    void updateVideoByUuid(String uuid, TransactionVideoDto transactionVideoDto);

    /**
     * This method is used to delete specific video resource by uuid
     * @param uuid is the path parameter from client
     */
    void deleteVideoByUuid(String uuid);

    /**
     * This method is used to upload specific video image by uuid
     * @param uuid is the path parameter from client
     * @param file is the request path from client
     * @return FileDto
     */
    FileDto uploadVideoImageByUuid(String uuid, MultipartFile file);

    /**
     * This method is used to load videos by authenticated author
     * @return List<VideoDto>
     */
    List<VideoDto> loadVideosByAuthenticatedAuthor();

}
