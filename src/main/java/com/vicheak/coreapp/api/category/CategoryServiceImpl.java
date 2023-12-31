package com.vicheak.coreapp.api.category;

import com.vicheak.coreapp.api.category.web.CategoryDto;
import com.vicheak.coreapp.api.course.CourseMapper;
import com.vicheak.coreapp.api.course.CourseRepository;
import com.vicheak.coreapp.api.course.web.CourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public List<CategoryDto> loadAllCategories() {
        return categoryMapper.fromCategoryToCategoryDto(categoryRepository.findAll());
    }

    @Override
    public CategoryDto loadCategoryByName(String name) {
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Category with name, %s has not been found in the system!"
                                        .formatted(name))
                );

        return categoryMapper.fromCategoryToCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto createNewCategory(CategoryDto categoryDto) {
        //check if category's name already exists
        if (categoryRepository.existsByNameIsIgnoreCase(categoryDto.name()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Category's name conflicts resource in the system!");

        //map from dto to entity category
        Category category = categoryMapper.fromCategoryDtoToCategory(categoryDto);

        return categoryMapper.fromCategoryToCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryByName(String name, CategoryDto categoryDto) {
        //check if the category does not exist
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Category with name, %s has not been found in the system!"
                                        .formatted(name))
                );

        //check if category's name already exists (except the previous name)
        if (Objects.nonNull(categoryDto.name()))
            if (!categoryDto.name().equalsIgnoreCase(category.getName()) &&
                    categoryRepository.existsByNameIsIgnoreCase(categoryDto.name()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Category's name conflicts resource in the system!");

        categoryMapper.fromCategoryDtoToCategory(category, categoryDto);

        return categoryMapper.fromCategoryToCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public void deleteCategoryByName(String name) {
        //check if the category does not exist
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Category with name, %s has not been found in the system!"
                                        .formatted(name))
                );

        categoryRepository.delete(category);
    }

    @Override
    public List<CourseDto> loadCoursesByCategoryName(String name) {
        return courseMapper.fromCourseToCourseDto(courseRepository.findByCategoryName(name));
    }

}
