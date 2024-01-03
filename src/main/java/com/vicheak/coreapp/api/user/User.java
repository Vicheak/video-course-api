package com.vicheak.coreapp.api.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_username", length = 150, unique = true, nullable = false)
    private String username;

    @Column(name = "user_email", length = 150, unique = true, nullable = false)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_gender", length = 50, nullable = false)
    private String gender;

    @Column(name = "user_phone_number", length = 100, unique = true, nullable = false)
    private String phoneNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "user_date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "user_photo")
    private String photo;

    @CreationTimestamp
    @Column(name = "user_join_date")
    private LocalDateTime joinDate;

    private Boolean verified;

    private String verifiedCode;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;

    private Boolean enabled;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserRole> userRoles;

}
