package com.course.schedule

import com.course.schedule.repository.StudentRepository
import com.course.schedule.service.AuthService
import com.course.schedule.service.ScheduleService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class ScheduleServiceTest {

    @Autowired lateinit var scheduleService: ScheduleService
    @Autowired lateinit var authService: AuthService
    @Autowired lateinit var studentRepository: StudentRepository

    @Test
    fun `authentication checks username and password`() {
        assertTrue(authService.authenticate("st1", "123"))
        assertTrue(authService.authenticate("st6", "sVrC"))
        assertFalse(authService.authenticate("st1", "wrong"))
        assertFalse(authService.authenticate("unknown", "123"))
    }

    @Test
    fun `student schedule contains subjects of his group with marks`() {
        val st1 = studentRepository.findById("st1").get()

        val schedule = scheduleService.getStudentSchedule(st1)

        assertEquals(setOf("Spirituality", "Space Travel"), schedule.map { it.subjectName }.toSet())
        val spirituality = schedule.first { it.subjectName == "Spirituality" }
        assertEquals("Maureen Garcia", spirituality.teacherName)
        assertEquals(3, spirituality.lessons.size)
        assertEquals(4, spirituality.lessons.first { it.lessonName == "practice 1" }.mark)
    }

    @Test
    fun `teacher subjects are grouped by subject name`() {
        val subjects = scheduleService.getTeacherSubjects("te1")

        assertEquals(setOf("Spirituality", "Space Travel", "Alien Crafting"), subjects.keys)
        assertEquals(listOf(2), subjects["Alien Crafting"]!!.map { it.id })
        assertEquals(3, subjects["Alien Crafting"]!![0].population)
    }

    @Test
    fun `teacher can set mark and comment`() {
        scheduleService.setMark("te1", 1, "Spirituality", "Les", "st1", 3, "be better")

        val st1 = studentRepository.findById("st1").get()
        val lesson = scheduleService.getStudentSchedule(st1)
            .first { it.subjectName == "Spirituality" }
            .lessons.first { it.lessonName == "Les" }
        assertEquals(3, lesson.mark)
        assertEquals("be better", lesson.comment)
    }

    @Test
    fun `new lesson gets empty result for every student of the group`() {
        scheduleService.addLesson("te2", 2, "Gardening", "practice X", LocalDate.of(2021, 1, 15))

        val journal = scheduleService.getLessonJournal("te2", 2, "Gardening", "practice X")
        assertNotNull(journal)
        val (info, rows) = journal
        assertEquals("15-01-2021", info.date)
        assertEquals(listOf("st2", "st4", "st6"), rows.map { it.studentUn })
        assertTrue(rows.all { it.mark == 0 })
    }

    @Test
    fun `lesson can be deleted`() {
        scheduleService.deleteLesson("te2", 2, "Gardening", "Remote lesson")

        assertTrue(scheduleService.getLessons("te2", 2, "Gardening").isEmpty())
    }
}
