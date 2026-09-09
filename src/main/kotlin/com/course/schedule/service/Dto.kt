package com.course.schedule.service

import com.fasterxml.jackson.annotation.JsonIgnore

/** Строка занятия в расписании студента: оценка и комментарий преподавателя. */
data class LessonEntry(
    val lessonName: String,
    val date: String,
    val mark: Int,
    val comment: String?,
) {
    /** Оценки 0/1 в оригинале отображались пустой ячейкой. */
    @get:JsonIgnore
    val markText: String
        get() = if (mark <= 1) "" else mark.toString()

    @get:JsonIgnore
    val commentText: String
        get() = comment ?: ""
}

/** Предмет в расписании студента (Subject в исходном проекте). */
data class SubjectView(
    val subjectName: String,
    val teacherName: String,
    val lessons: List<LessonEntry>,
)

/** Ссылка на группу в списке предметов преподавателя. */
data class GroupRef(
    val id: Int,
    val population: Int,
)

/** Строка журнала занятия у преподавателя. */
data class JournalRow(
    val studentUn: String,
    val studentName: String,
    val mark: Int,
    val comment: String?,
) {
    @get:JsonIgnore
    val markText: String
        get() = if (mark <= 1) "" else mark.toString()

    @get:JsonIgnore
    val commentText: String
        get() = comment ?: ""
}

/** Сгенерированные логин и пароль нового пользователя (показываются один раз). */
data class Credentials(
    val username: String,
    val password: String,
)
