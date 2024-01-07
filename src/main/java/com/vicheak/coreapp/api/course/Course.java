package com.vicheak.coreapp.api.course;

import com.vicheak.coreapp.api.category.Category;
import com.vicheak.coreapp.api.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "course_title", length = 150, unique = true, nullable = false)
    private String title;

    @Column(name = "course_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "course_image")
    private String image;

    @Column(name = "course_duration_in_hour")
    private Integer durationInHour;

    @Column(name = "course_cost")
    private BigDecimal cost;

    @Column(name = "number_of_view")
    private Long numberOfView;

    @Column(name = "number_of_like")
    private Long numberOfLike;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
