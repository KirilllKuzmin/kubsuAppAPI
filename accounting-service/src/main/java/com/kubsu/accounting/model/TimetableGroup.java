package com.kubsu.accounting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_types")
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
