package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "types_of_work", schema = "accounting_schema")
public class TypeOfWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
}
