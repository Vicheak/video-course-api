package com.vicheak.coreapp.api.category;

import com.vicheak.coreapp.api.category.web.CategoryDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto fromCategoryToCategoryDto(Category category);

    List<CategoryDto> fromCategoryToCategoryDto(List<Category> categories);

    Category fromCategoryDtoToCategory(CategoryDto categoryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromCategoryDtoToCategory(@MappingTarget Category category, CategoryDto categoryDto);

}
