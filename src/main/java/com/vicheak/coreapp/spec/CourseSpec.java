package com.vicheak.coreapp.spec;

import com.vicheak.coreapp.api.course.Course;
import com.vicheak.coreapp.api.course.Course_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@RequiredArgsConstructor
public class CourseSpec implements Specification<Course> {

    private final CourseFilter courseFilter;

    @Override
    public Predicate toPredicate(@NonNull Root<Course> courseRoot,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(courseFilter.title())) {
            Predicate predicateTitle = cb.like(cb.lower(courseRoot.get(Course_.TITLE)),
                    courseFilter.title().toLowerCase() + '%');
            predicates.add(predicateTitle);
        }

        if (Objects.nonNull(courseFilter.durationInHour())) {
            Predicate predicateDurationInHour = cb.equal(courseRoot.get(Course_.DURATION_IN_HOUR),
                    courseFilter.durationInHour());
            predicates.add(predicateDurationInHour);
        }

        if (Objects.nonNull(courseFilter.cost())) {
            Predicate predicateCost = cb.equal(courseRoot.get(Course_.COST),
                    courseFilter.cost());
            predicates.add(predicateCost);
        }

        //convert from list to array
        return cb.and(predicates.toArray(Predicate[]::new));
    }

}
