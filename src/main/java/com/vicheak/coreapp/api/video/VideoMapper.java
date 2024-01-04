package com.vicheak.coreapp.api.video;

import com.vicheak.coreapp.api.video.web.TransactionVideoDto;
import com.vicheak.coreapp.api.video.web.VideoDto;
import com.vicheak.coreapp.util.ValueInjectUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class VideoMapper {

    protected ValueInjectUtil valueInjectUtil;

    @Autowired
    public void setValueInjectUtil(ValueInjectUtil valueInjectUtil) {
        this.valueInjectUtil = valueInjectUtil;
    }

    @Mapping(target = "course", source = "course.title")
    @Mapping(target = "author", source = "user.username")
    @Mapping(target = "imageUri", expression = "java(valueInjectUtil.getImageUri(video.getImageCover()))")
    public abstract VideoDto fromVideoToVideoDto(Video video);

    public abstract List<VideoDto> fromVideoToVideoDto(List<Video> videos);

    @Mapping(target = "course.id", source = "courseId")
    @Mapping(target = "user.id", source = "userId")
    public abstract Video fromTransactionVideoDtoToVideo(TransactionVideoDto transactionVideoDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void fromTransactionVideoDtoToVideo(@MappingTarget Video video, TransactionVideoDto transactionVideoDto);

}
