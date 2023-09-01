package com.kubsu.userService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Faculties")
@NoArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    public Faculty(String name) {
        this.name = name;
    }
}
