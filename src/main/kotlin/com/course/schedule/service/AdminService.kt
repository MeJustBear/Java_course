package com.course.schedule.service

import com.course.schedule.domain.LessonResult
import com.course.schedule.domain.Student
import com.course.schedule.domain.StudyGroup
import com.course.schedule.domain.SubjectAssignment
import com.course.schedule.domain.Teacher
import com.course.schedule.repository.LessonResultRepository
import com.course.schedule.repository.StudentRepository
import com.course.schedule.repository.StudyGroupRepository
import com.course.schedule.repository.SubjectAssignmentRepository
import com.course.schedule.repository.TeacherRepository
import com.course.schedule.repository.WorkerRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Административные операции: CRUD групп, студентов, преподавателей и предметов
 * (admin-ветка MainServlet + add/removeWorker, add/removeGroup, add/removeSubject
 * из исходного DBConnector).
 */
@Service
class AdminService(
    private val workerRepository: WorkerRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val groupRepository: StudyGroupRepository,
    private val assignmentRepository: SubjectAssignmentRepository,
    private val lessonResultRepository: LessonResultRepository,
) {

    @Transactional(readOnly = true)
    fun getGroups(): List<GroupRef> =
        groupRepository.findAll().sortedBy { it.id }.map { GroupRef(it.id, it.students.size) }

    /** Студенты группы (коллекция инициализируется внутри транзакции). */
    @Transactional(readOnly = true)
    fun getGroupStudents(groupId: Int): List<Student>? {
        val group = groupRepository.findById(groupId).orElse(null) ?: return null
        return group.students.sortedBy { it.un }
    }

    @Transactional(readOnly = true)
    fun getTeachers(): List<Teacher> = teacherRepository.findAll().sortedBy { it.un }

    /** Денормализованный список предметов преподавателя (Teacher.subjects в оригинале). */
    @Transactional(readOnly = true)
    fun getTeacherSubjectNames(): Map<String, List<String>> {
        val byTeacher = assignmentRepository.findAll().groupBy { it.teacher?.un }
        return teacherRepository.findAll().associate { teacher ->
            teacher.un to (byTeacher[teacher.un]?.map { it.subjectName }?.distinct() ?: emptyList())
        }
    }

    @Transactional(readOnly = true)
    fun nextGroupId(): Int = (groupRepository.findAll().maxOfOrNull { it.id } ?: 0) + 1

    @Transactional
    fun addGroup(groupId: Int) {
        if (!groupRepository.existsById(groupId)) {
            groupRepository.save(StudyGroup(id = groupId))
        }
    }

    /** Удаляет группу вместе со студентами и назначениями предметов. */
    @CacheEvict(
        cacheNames = [ScheduleService.STUDENT_SCHEDULE_CACHE, ScheduleService.TEACHER_SUBJECTS_CACHE],
        allEntries = true,
    )
    @Transactional
    fun removeGroup(groupId: Int) {
        val group = groupRepository.findById(groupId).orElse(null) ?: return
        assignmentRepository.deleteAll(assignmentRepository.findByGroupId(groupId))
        group.students.forEach { student ->
            lessonResultRepository.deleteByStudentUn(student.un)
            workerRepository.delete(student)
        }
        groupRepository.delete(group)
    }

    /**
     * Добавляет студента: логин `st{next}`, случайный пароль, пустые результаты
     * во всех существующих занятиях группы. Имя и фамилия — только буквы.
     */
    @CacheEvict(
        cacheNames = [ScheduleService.STUDENT_SCHEDULE_CACHE, ScheduleService.TEACHER_SUBJECTS_CACHE],
        allEntries = true,
    )
    @Transactional
    fun addStudent(groupId: Int, name: String, surname: String): Credentials? {
        if (!isValidName(name) || !isValidName(surname)) return null
        val group = groupRepository.findById(groupId).orElse(null) ?: return null
        val un = "st" + nextWorkerId("st")
        val password = generatePassword()
        val student = studentRepository.save(Student(un, name, surname, password, group))
        assignmentRepository.findByGroupId(groupId).forEach { assignment ->
            assignment.lessons.forEach { lesson ->
                lesson.results.add(LessonResult(lesson = lesson, student = student))
            }
        }
        return Credentials(un, password)
    }

    @CacheEvict(
        cacheNames = [ScheduleService.STUDENT_SCHEDULE_CACHE, ScheduleService.TEACHER_SUBJECTS_CACHE],
        allEntries = true,
    )
    @Transactional
    fun removeStudent(studentUn: String) {
        val student = studentRepository.findById(studentUn).orElse(null) ?: return
        lessonResultRepository.deleteByStudentUn(student.un)
        workerRepository.delete(student)
    }

    @Transactional
    fun addTeacher(name: String, surname: String): Credentials? {
        if (!isValidName(name) || !isValidName(surname)) return null
        val un = "te" + nextWorkerId("te")
        val password = generatePassword()
        teacherRepository.save(Teacher(un, name, surname, password))
        return Credentials(un, password)
    }

    /** Удаляет преподавателя вместе с его назначениями (занятия и оценки — каскадно). */
    @CacheEvict(
        cacheNames = [ScheduleService.STUDENT_SCHEDULE_CACHE, ScheduleService.TEACHER_SUBJECTS_CACHE],
        allEntries = true,
    )
    @Transactional
    fun removeTeacher(teacherUn: String) {
        val teacher = teacherRepository.findById(teacherUn).orElse(null) ?: return
        assignmentRepository.deleteAll(assignmentRepository.findByTeacherUn(teacherUn))
        workerRepository.delete(teacher)
    }

    /**
     * Назначает предмет преподавателю на группу. Если предмет в группе уже вёл
     * другой преподаватель — назначение переходит новому (как в оригинале).
     */
    @CacheEvict(
        cacheNames = [ScheduleService.STUDENT_SCHEDULE_CACHE, ScheduleService.TEACHER_SUBJECTS_CACHE],
        allEntries = true,
    )
    @Transactional
    fun assignSubject(subjectName: String, teacherUn: String, groupId: Int) {
        if (!isValidName(subjectName)) return
        val teacher = teacherRepository.findById(teacherUn).orElse(null) ?: return
        val group = groupRepository.findById(groupId).orElse(null) ?: return
        val existing = assignmentRepository.findFirstBySubjectNameAndGroupId(subjectName, groupId)
        if (existing != null) {
            existing.teacher = teacher
        } else {
            assignmentRepository.save(
                SubjectAssignment(subjectName = subjectName, teacher = teacher, group = group),
            )
        }
    }

    @CacheEvict(
        cacheNames = [ScheduleService.STUDENT_SCHEDULE_CACHE, ScheduleService.TEACHER_SUBJECTS_CACHE],
        allEntries = true,
    )
    @Transactional
    fun removeSubject(teacherUn: String, subjectName: String) {
        assignmentRepository.findFirstByTeacherUnAndSubjectName(teacherUn, subjectName)
            ?.let { assignmentRepository.delete(it) }
    }

    private fun nextWorkerId(prefix: String): Int =
        workerRepository.findAll()
            .filter { it.un.startsWith(prefix) }
            .maxOfOrNull { it.un.substring(2).toIntOrNull() ?: 0 }
            ?.plus(1) ?: 1

    /** 4 случайных символа [0-9A-Za-z], как в оригинальном addWorker. */
    private fun generatePassword(): String {
        val chars = ('0'..'9') + ('A'..'Z') + ('a'..'z')
        return (1..4).map { chars.random() }.joinToString("")
    }

    private fun isValidName(value: String): Boolean =
        value.isNotBlank() && value.all { it.isLetter() || it == ' ' }
}
