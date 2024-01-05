package com.vicheak.coreapp.api.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Optional<Course> findByUuid(String uuid);

    boolean existsByTitleIgnoreCase(String title);

    List<Course> findByCategoryName(String name);

}
