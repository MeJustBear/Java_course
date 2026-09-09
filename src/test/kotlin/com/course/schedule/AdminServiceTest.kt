package com.course.schedule

import com.course.schedule.repository.LessonResultRepository
import com.course.schedule.repository.StudentRepository
import com.course.schedule.repository.StudyGroupRepository
import com.course.schedule.repository.SubjectAssignmentRepository
import com.course.schedule.repository.TeacherRepository
import com.course.schedule.repository.WorkerRepository
import com.course.schedule.service.AdminService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Порт исходных тестов myDataBaseTest: removeWorker(Student),
 * addWorker(Teacher), removeGroup — поверх ORM и H2.
 */
@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired lateinit var adminService: AdminService
    @Autowired lateinit var workerRepository: WorkerRepository
    @Autowired lateinit var studentRepository: StudentRepository
    @Autowired lateinit var teacherRepository: TeacherRepository
    @Autowired lateinit var groupRepository: StudyGroupRepository
    @Autowired lateinit var assignmentRepository: SubjectAssignmentRepository
    @Autowired lateinit var lessonResultRepository: LessonResultRepository

    @Test
    fun `remove student deletes him from group, journal and credentials`() {
        assertTrue(workerRepository.existsById("st1"))

        adminService.removeStudent("st1")

        assertFalse(workerRepository.existsById("st1"))
        assertTrue(groupRepository.findById(1).get().students.none { it.un == "st1" })
        assertTrue(lessonResultRepository.findAll().none { it.student?.un == "st1" })
    }

    @Test
    fun `add teacher generates next id and password`() {
        val credentials = adminService.addTeacher("John", "Doe")

        assertNotNull(credentials)
        assertEquals("te3", credentials.username)
        assertEquals(4, credentials.password.length)
        val saved = teacherRepository.findById("te3").get()
        assertEquals("John Doe", saved.fullName)
        assertEquals(credentials.password, saved.password)
    }

    @Test
    fun `add worker with non-letter name is rejected`() {
        assertNull(adminService.addTeacher("R2D2", "Droid"))
        assertNull(adminService.addStudent(1, "Bob1", "Smith"))
    }

    @Test
    fun `remove group deletes its students and subject assignments`() {
        adminService.removeGroup(2)

        assertTrue(groupRepository.findById(2).isEmpty)
        assertFalse(workerRepository.existsById("st2"))
        assertFalse(workerRepository.existsById("st4"))
        assertFalse(workerRepository.existsById("st6"))
        assertTrue(assignmentRepository.findByGroupId(2).isEmpty())
        // группа 1 не затронута
        assertTrue(workerRepository.existsById("st1"))
        assertEquals(2, assignmentRepository.findByGroupId(1).size)
    }

    @Test
    fun `add student creates empty results in all group lessons`() {
        val credentials = adminService.addStudent(1, "New", "Student")

        assertNotNull(credentials)
        assertEquals("st7", credentials.username)
        val student = studentRepository.findById("st7").get()
        assertEquals(1, student.group?.id)
        // 3 занятия Spirituality + 2 занятия Space Travel
        val results = lessonResultRepository.findAll().filter { it.student?.un == "st7" }
        assertEquals(5, results.size)
        assertTrue(results.all { it.mark == 0 })
    }

    @Test
    fun `assign existing subject transfers it to another teacher`() {
        adminService.assignSubject("Gardening", "te1", 2)

        val assignment = assignmentRepository.findFirstBySubjectNameAndGroupId("Gardening", 2)
        assertNotNull(assignment)
        assertEquals("te1", assignment.teacher?.un)
        // занятия предмета сохранились
        assertEquals(1, assignment.lessons.size)
    }

    @Test
    fun `remove teacher deletes his assignments`() {
        adminService.removeTeacher("te2")

        assertFalse(workerRepository.existsById("te2"))
        assertNull(assignmentRepository.findFirstBySubjectNameAndGroupId("Gardening", 2))
    }
}
