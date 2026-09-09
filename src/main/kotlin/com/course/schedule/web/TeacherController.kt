package com.course.schedule.web

import com.course.schedule.domain.Teacher
import com.course.schedule.service.AuthService
import com.course.schedule.service.ScheduleService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

/**
 * Страницы преподавателя: занятия предмета в группе и журнал занятия
 * (teacher-ветка MainServlet). URL-схема исходного проекта сохранена:
 * пробелы в названиях кодируются подчёркиванием.
 */
@Controller
class TeacherController(
    private val authService: AuthService,
    private val scheduleService: ScheduleService,
) {

    @GetMapping("/schedule/gr{groupId}/{subject}/")
    fun subjectPage(
        @PathVariable groupId: Int,
        @PathVariable subject: String,
        session: HttpSession,
        model: Model,
    ): String {
        val teacher = currentTeacher(session) ?: return "no_permission"
        val subjectName = decode(subject)
        model.addAttribute("worker", teacher)
        model.addAttribute("groupId", groupId)
        model.addAttribute("subjectName", subjectName)
        model.addAttribute("subjectUrl", subject)
        model.addAttribute("lessons", scheduleService.getLessons(teacher.un, groupId, subjectName))
        return "teacher_subject"
    }

    @GetMapping("/schedule/gr{groupId}/{subject}/{lesson}/")
    fun lessonPage(
        @PathVariable groupId: Int,
        @PathVariable subject: String,
        @PathVariable lesson: String,
        session: HttpSession,
        model: Model,
    ): String {
        val teacher = currentTeacher(session) ?: return "no_permission"
        val subjectName = decode(subject)
        val lessonName = decode(lesson)
        val (lessonInfo, journal) = scheduleService
            .getLessonJournal(teacher.un, groupId, subjectName, lessonName)
            ?: return "redirect:/schedule/"
        model.addAttribute("worker", teacher)
        model.addAttribute("groupId", groupId)
        model.addAttribute("subjectName", subjectName)
        model.addAttribute("lessonInfo", lessonInfo)
        model.addAttribute("journal", journal)
        return "teacher_lesson"
    }

    /** Выставление оценки и комментария студенту. */
    @PostMapping("/schedule/gr{groupId}/{subject}/{lesson}/")
    fun setMark(
        @PathVariable groupId: Int,
        @PathVariable subject: String,
        @PathVariable lesson: String,
        @RequestParam("Student") studentUn: String,
        @RequestParam("Mark") mark: Int,
        @RequestParam("Comment") comment: String?,
        session: HttpSession,
    ): String {
        val teacher = currentTeacher(session) ?: return "no_permission"
        scheduleService.setMark(teacher.un, groupId, decode(subject), decode(lesson), studentUn, mark, comment)
        return "redirect:/schedule/gr$groupId/$subject/$lesson/"
    }

    /** Создание занятия (в форме есть параметр Date, как в оригинале). */
    @PostMapping("/schedule/gr{groupId}/{subject}/", params = ["Date"])
    fun addLesson(
        @PathVariable groupId: Int,
        @PathVariable subject: String,
        @RequestParam("Date") date: LocalDate,
        @RequestParam("LessonName") lessonName: String,
        session: HttpSession,
    ): String {
        val teacher = currentTeacher(session) ?: return "no_permission"
        scheduleService.addLesson(teacher.un, groupId, decode(subject), lessonName.trim(), date)
        return "redirect:/schedule/gr$groupId/$subject/"
    }

    /** Удаление занятия (в форме есть параметр Lesson). */
    @PostMapping("/schedule/gr{groupId}/{subject}/", params = ["Lesson"])
    fun deleteLesson(
        @PathVariable groupId: Int,
        @PathVariable subject: String,
        @RequestParam("Lesson") lessonName: String,
        session: HttpSession,
    ): String {
        val teacher = currentTeacher(session) ?: return "no_permission"
        scheduleService.deleteLesson(teacher.un, groupId, decode(subject), lessonName)
        return "redirect:/schedule/gr$groupId/$subject/"
    }

    private fun currentTeacher(session: HttpSession): Teacher? =
        authService.findWorker(session.getAttribute("name") as String) as? Teacher

    private fun decode(value: String): String = value.replace('_', ' ')
}
