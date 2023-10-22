package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "work_dates", schema = "accounting_schema")
public class WorkDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @Column(name = "work_date")
    private OffsetDateTime workDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_of_work_id")
    private TypeOfWork typeOfWork;

    public WorkDate(Timetable timetable, OffsetDateTime workDate, TypeOfWork typeOfWork) {
        this.timetable = timetable;
        this.workDate = workDate;
        this.typeOfWork = typeOfWork;
    }
}