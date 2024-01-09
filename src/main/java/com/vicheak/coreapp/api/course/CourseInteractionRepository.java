package com.vicheak.coreapp.api.course;

import com.vicheak.coreapp.api.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseInteractionRepository extends JpaRepository<CourseInteraction, Long> {

    Optional<CourseInteraction> findByCourseAndUser(Course course, User user);

}
