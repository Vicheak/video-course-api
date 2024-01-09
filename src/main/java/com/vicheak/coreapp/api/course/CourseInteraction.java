package com.vicheak.coreapp.api.course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vicheak.coreapp.api.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "courses_interations")
public class CourseInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_interaction_id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_viewed", nullable = false)
    private Boolean isViewed;

    @Column(name = "is_liked", nullable = false)
    private Boolean isLiked;

}
