package com.vicheak.coreapp.api.authority;

import com.vicheak.coreapp.api.user.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", length = 100, unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_authorities",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "auth_id", referencedColumnName = "authority_id"))
    private Set<Authority> authorities;

}
