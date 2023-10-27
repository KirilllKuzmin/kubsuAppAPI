package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "evaluation_types", schema = "accounting_schema")
public class EvaluationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluation_name")
    private String evaluationName;

    @Column(name = "evaluation_number")
    private Long evaluationNumber;
}
