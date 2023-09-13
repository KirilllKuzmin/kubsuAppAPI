package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "academic_buildings", schema = "accounting_schema")
public class AcademicBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column
    private String name;
}
