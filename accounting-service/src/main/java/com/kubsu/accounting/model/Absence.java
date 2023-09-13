package com.kubsu.accounting.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "absences", schema = "accounting_schema")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "event_date")
    private OffsetDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "absence_type_id")
    private AbsenceType absenceType;
}
