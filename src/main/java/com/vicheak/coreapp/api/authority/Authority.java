package com.vicheak.coreapp.api.authority;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Integer id;

    @Column(name = "authority_name", length = 120, unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;

}
