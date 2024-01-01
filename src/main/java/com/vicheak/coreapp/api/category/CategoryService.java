package com.vicheak.coreapp.api.category;

import com.vicheak.coreapp.api.category.web.CategoryDto;

import java.util.List;

public interface CategoryService {

    /**
     * This method is used to load all category resources from the system
     * @return List<CategoryDto>
     */
    List<CategoryDto> loadAllCategories();

    /**
     * This method is used to load specific category resource by name
     * @param name is the path parameter from client
     * @return CategoryDto
     */
    CategoryDto loadCategoryByName(String name);

    /**
     * This method is used to create new category resource into the system
     * @param categoryDto is the request from client
     * @return CategoryDto
     */
    CategoryDto createNewCategory(CategoryDto categoryDto);

    /**
     * This method is used to update specific category resource by name
     * @param name is the path parameter from client
     * @param categoryDto is the request from client
     * @return CategoryDto
     */
    CategoryDto updateCategoryByName(String name, CategoryDto categoryDto);

    /**
     * This method is used to delete specific category resource by name
     * @param name is the path parameter from client
     */
    void deleteCategoryByName(String name);

}
