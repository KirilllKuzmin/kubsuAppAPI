package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "academic_degrees", schema = "accounting_schema")
public class AcademicDegree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column
    private String name;
}
