package com.course.schedule.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table

/**
 * Назначение «предмет + преподаватель + группа» (dataNode в исходном проекте)
 * со списком занятий.
 */
@Entity
@Table(name = "subject_assignment")
class SubjectAssignment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var subjectName: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_un")
    var teacher: Teacher? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    var group: StudyGroup? = null,

    @OneToMany(mappedBy = "assignment", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id")
    var lessons: MutableList<Lesson> = mutableListOf(),
)
