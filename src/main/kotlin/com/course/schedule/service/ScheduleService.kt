package com.course.schedule.service

import com.course.schedule.domain.Lesson
import com.course.schedule.domain.LessonResult
import com.course.schedule.domain.Student
import com.course.schedule.repository.StudentRepository
import com.course.schedule.repository.SubjectAssignmentRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Расписание и оценки: сценарии студента и преподавателя
 * (getSubjects / getSubjectsForTeachers / correctStudent / makeNewLesson / deleteLesson
 * из исходного DBConnector). Чтение кешируется в Redis.
 */
@Service
class ScheduleService(
    private val assignmentRepository: SubjectAssignmentRepository,
    private val studentRepository: StudentRepository,
) {
    private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    /** Расписание студента: предметы его группы с занятиями и его оценками. */
    @Cacheable(STUDENT_SCHEDULE_CACHE, key = "#student.un")
    @Transactional(readOnly = true)
    fun getStudentSchedule(student: Student): List<SubjectView> {
        val groupId = student.group?.id ?: return emptyList()
        return assignmentRepository.findByGroupId(groupId).map { assignment ->
            SubjectView(
                subjectName = assignment.subjectName,
                teacherName = assignment.teacher?.fullName ?: "",
                lessons = assignment.lessons.map { lesson ->
                    val result = lesson.results.find { it.student?.un == student.un }
                    LessonEntry(
                        lessonName = lesson.name,
                        date = lesson.date.format(dateFormat),
                        mark = result?.mark ?: 0,
                        comment = result?.comment,
                    )
                },
            )
        }
    }

    /** Предметы преподавателя: название → группы, где он его ведёт. */
    @Cacheable(TEACHER_SUBJECTS_CACHE, key = "#teacherUn")
    @Transactional(readOnly = true)
    fun getTeacherSubjects(teacherUn: String): Map<String, List<GroupRef>> =
        assignmentRepository.findByTeacherUn(teacherUn)
            .groupBy { it.subjectName }
            .mapValues { (_, assignments) ->
                assignments.mapNotNull { a -> a.group?.let { GroupRef(it.id, it.students.size) } }
            }

    /** Занятия предмета в конкретной группе (страница преподавателя). */
    @Transactional(readOnly = true)
    fun getLessons(teacherUn: String, groupId: Int, subjectName: String): List<LessonEntry> {
        val assignment = assignmentRepository
            .findFirstByTeacherUnAndSubjectNameAndGroupId(teacherUn, subjectName, groupId)
            ?: return emptyList()
        return assignment.lessons.map {
            LessonEntry(it.name, it.date.format(dateFormat), 0, null)
        }
    }

    /** Журнал занятия: студенты группы с оценками и комментариями. */
    @Transactional(readOnly = true)
    fun getLessonJournal(
        teacherUn: String,
        groupId: Int,
        subjectName: String,
        lessonName: String,
    ): Pair<LessonEntry, List<JournalRow>>? {
        val lesson = findLesson(teacherUn, groupId, subjectName, lessonName) ?: return null
        val rows = lesson.results
            .filter { it.student != null }
            .sortedBy { it.student!!.un }
            .map { JournalRow(it.student!!.un, it.student!!.fullName, it.mark, it.comment) }
        return LessonEntry(lesson.name, lesson.date.format(dateFormat), 0, null) to rows
    }

    /** Выставить оценку и комментарий (correctStudent в оригинале). */
    @CacheEvict(cacheNames = [STUDENT_SCHEDULE_CACHE, TEACHER_SUBJECTS_CACHE], allEntries = true)
    @Transactional
    fun setMark(
        teacherUn: String,
        groupId: Int,
        subjectName: String,
        lessonName: String,
        studentUn: String,
        mark: Int,
        comment: String?,
    ) {
        val lesson = findLesson(teacherUn, groupId, subjectName, lessonName) ?: return
        val result = lesson.results.find { it.student?.un == studentUn } ?: return
        result.mark = mark
        result.comment = comment
    }

    /**
     * Создать занятие; каждому студенту группы создаётся пустой результат
     * (makeNewLesson + pushBackLesson в оригинале).
     */
    @CacheEvict(cacheNames = [STUDENT_SCHEDULE_CACHE, TEACHER_SUBJECTS_CACHE], allEntries = true)
    @Transactional
    fun addLesson(teacherUn: String, groupId: Int, subjectName: String, lessonName: String, date: LocalDate) {
        val assignment = assignmentRepository
            .findFirstByTeacherUnAndSubjectNameAndGroupId(teacherUn, subjectName, groupId)
            ?: return
        val lesson = Lesson(name = lessonName, date = date, assignment = assignment)
        studentRepository.findByGroupId(groupId).forEach { student ->
            lesson.results.add(LessonResult(lesson = lesson, student = student))
        }
        assignment.lessons.add(lesson)
    }

    /** Удалить занятие вместе с результатами (deleteLesson в оригинале). */
    @CacheEvict(cacheNames = [STUDENT_SCHEDULE_CACHE, TEACHER_SUBJECTS_CACHE], allEntries = true)
    @Transactional
    fun deleteLesson(teacherUn: String, groupId: Int, subjectName: String, lessonName: String) {
        val assignment = assignmentRepository
            .findFirstByTeacherUnAndSubjectNameAndGroupId(teacherUn, subjectName, groupId)
            ?: return
        assignment.lessons.removeIf { it.name == lessonName }
    }

    private fun findLesson(teacherUn: String, groupId: Int, subjectName: String, lessonName: String): Lesson? =
        assignmentRepository
            .findFirstByTeacherUnAndSubjectNameAndGroupId(teacherUn, subjectName, groupId)
            ?.lessons?.find { it.name == lessonName }

    companion object {
        const val STUDENT_SCHEDULE_CACHE = "studentSchedule"
        const val TEACHER_SUBJECTS_CACHE = "teacherSubjects"
    }
}
