package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evaluations", schema = "accounting_schema")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "evaluation_date")
    private OffsetDateTime evaluationDate;

    @Column(name = "event_date")
    private OffsetDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_type_id")
    private EvaluationType evaluationType;

    public Evaluation(Timetable timetable, Student student, OffsetDateTime evaluationDate, OffsetDateTime eventDate, EvaluationType evaluationType) {
        this.timetable = timetable;
        this.student = student;
        this.evaluationDate = evaluationDate;
        this.eventDate = eventDate;
        this.evaluationType = evaluationType;
    }
}
