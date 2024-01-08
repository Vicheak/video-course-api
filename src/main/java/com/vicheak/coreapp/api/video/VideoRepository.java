package com.vicheak.coreapp.api.video;

import com.vicheak.coreapp.api.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {

    Optional<Video> findByUuid(String uuid);

    List<Video> findByCourseUuid(String uuid);

    List<Video> findByCourse(Course course);

}
