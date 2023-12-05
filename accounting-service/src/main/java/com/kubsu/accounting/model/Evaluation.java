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
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "evaluation_date")
    private OffsetDateTime evaluationDate;

    @Column(name = "event_date")
    private OffsetDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_grade_id")
    private EvaluationGrade evaluationGrade;

    public Evaluation(WorkDate workDate, Student student, OffsetDateTime evaluationDate, OffsetDateTime eventDate, EvaluationGrade evaluationGrade) {
        this.workDate = workDate;
        this.student = student;
        this.evaluationDate = evaluationDate;
        this.eventDate = eventDate;
        this.evaluationGrade = evaluationGrade;
    }
}
