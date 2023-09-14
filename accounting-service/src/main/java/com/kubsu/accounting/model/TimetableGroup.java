package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "timetable_groups", schema = "accounting_schema")
@Data
@NoArgsConstructor
public class TimetableGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @Column(name = "group_id")
    private Long groupId;
}
