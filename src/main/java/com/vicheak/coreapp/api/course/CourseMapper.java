package com.vicheak.coreapp.api.course;

import com.vicheak.coreapp.api.course.web.CourseDto;
import com.vicheak.coreapp.api.course.web.TransactionCourseDto;
import com.vicheak.coreapp.util.ValueInjectUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CourseMapper {

    protected ValueInjectUtil valueInjectUtil;

    @Autowired
    public void setValueInjectUtil(ValueInjectUtil valueInjectUtil) {
        this.valueInjectUtil = valueInjectUtil;
    }

    @Mapping(target = "category.id", source = "categoryId")
    public abstract Course fromTransactionCourseDtoToCourse(TransactionCourseDto transactionCourseDto);

    @Mapping(target = "category", source = "course.category.name")
    @Mapping(target = "author", source = "user.username")
    @Mapping(target = "imageUri", expression = "java(valueInjectUtil.getImageUri(course.getImage()))")
    public abstract CourseDto fromCourseToCourseDto(Course course);

    public abstract List<CourseDto> fromCourseToCourseDto(List<Course> courses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void fromTransactionCourseDtoToCourse(@MappingTarget Course course, TransactionCourseDto transactionCourseDto);

}
