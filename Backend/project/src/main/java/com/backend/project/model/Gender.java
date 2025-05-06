package com.backend.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "gender")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Gender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String mask;

    @Column(nullable = false)
    private boolean state = true;

    public Gender(String name, String mask, boolean state) {
        this.name = name;
        this.mask = mask;
        this.state = state;
    }
}
