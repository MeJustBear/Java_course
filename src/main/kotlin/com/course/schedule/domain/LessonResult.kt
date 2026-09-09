package com.course.schedule.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Результат студента на занятии (lesson.lessonNode в исходном проекте).
 * mark = 0 означает «оценка не выставлена».
 */
@Entity
@Table(name = "lesson_result")
class LessonResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    var lesson: Lesson? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_un")
    var student: Student? = null,

    var mark: Int = 0,

    var comment: String? = null,
)
