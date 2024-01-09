package com.vicheak.coreapp.api.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseInteractionRepository extends JpaRepository<CourseInteraction, Long> {

    Optional<CourseInteraction> findByCourseIdAndUserId(Long courseId, Long userId);

}
