package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "classrooms", schema = "accounting_schema")
public class Classroom {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "classroom_number")
    private String classroomNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "academic_building_id")
    private AcademicBuilding academicBuilding;
}
