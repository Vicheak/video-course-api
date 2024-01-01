package com.vicheak.coreapp.api.course;

import com.vicheak.coreapp.api.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
