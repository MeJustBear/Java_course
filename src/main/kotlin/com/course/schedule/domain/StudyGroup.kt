package com.course.schedule.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

/** Учебная группа (Group в исходном проекте). Номер группы задаёт администратор. */
@Entity
@Table(name = "study_group")
class StudyGroup(
    @Id
    var id: Int = 0,

    @OneToMany(mappedBy = "group")
    var students: MutableList<Student> = mutableListOf(),
)
