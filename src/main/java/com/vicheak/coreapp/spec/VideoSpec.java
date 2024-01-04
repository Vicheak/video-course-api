package com.vicheak.coreapp.spec;

import com.vicheak.coreapp.api.video.Video;
import com.vicheak.coreapp.api.video.Video_;
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
public class VideoSpec implements Specification<Video> {

    private final VideoFilter videoFilter;

    @Override
    public Predicate toPredicate(@NonNull Root<Video> videoRoot,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(videoFilter.title())) {
            Predicate predicateTitle = cb.like(cb.lower(videoRoot.get(Video_.TITLE)),
                    videoFilter.title().toLowerCase() + '%');
            predicates.add(predicateTitle);
        }

        if (Objects.nonNull(videoFilter.fromDate())) {
            Predicate predicateFromDate = cb.greaterThanOrEqualTo(videoRoot.get(Video_.CREATED_AT),
                    videoFilter.fromDate());
            predicates.add(predicateFromDate);
        }

        if (Objects.nonNull(videoFilter.toDate())) {
            Predicate predicateToDate = cb.lessThanOrEqualTo(videoRoot.get(Video_.CREATED_AT),
                    videoFilter.toDate());
            predicates.add(predicateToDate);
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }

}
