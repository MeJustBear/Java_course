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
import jakarta.persistence.Table
import java.time.LocalDate

/** Занятие по предмету (lesson в исходном проекте). */
@Entity
@Table(name = "lesson")
class Lesson(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var name: String = "",

    var date: LocalDate = LocalDate.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    var assignment: SubjectAssignment? = null,

    @OneToMany(mappedBy = "lesson", cascade = [CascadeType.ALL], orphanRemoval = true)
    var results: MutableList<LessonResult> = mutableListOf(),
)
