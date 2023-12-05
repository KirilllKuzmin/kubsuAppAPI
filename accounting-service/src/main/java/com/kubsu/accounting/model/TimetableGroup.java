package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "timetable_groups", schema = "accounting_schema")
@Setter
@NoArgsConstructor
public class TimetableGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @Getter
    @Column(name = "group_id")
    private Long groupId;
}
