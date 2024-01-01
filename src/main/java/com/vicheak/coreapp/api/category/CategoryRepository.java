package com.vicheak.coreapp.api.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIsIgnoreCase(String name);

}
