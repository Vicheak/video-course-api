package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.course.Course;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscription_details", uniqueConstraints = @UniqueConstraint(
        columnNames = {"course_id", "subscription_id"}
))
public class SubscriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_detail_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "subscription_cost", nullable = false)
    private BigDecimal cost;

    private Boolean approved;

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

}
