package com.vicheak.coreapp.api.video;

import com.vicheak.coreapp.api.course.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long id;

    @Column(name = "video_uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "video_title", nullable = false)
    private String title;

    @Column(name = "video_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_link", nullable = false)
    private String videoLink;

    @Column(name = "video_image_cover")
    private String imageCover;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

}
