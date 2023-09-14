package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "semesters", schema = "accounting_schema")
@Data
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column
    private String name;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;
}
