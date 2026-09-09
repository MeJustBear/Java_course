package com.course.schedule.web

import com.course.schedule.domain.Admin
import com.course.schedule.domain.Student
import com.course.schedule.domain.Teacher
import com.course.schedule.service.AuthService
import com.course.schedule.service.ScheduleService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/**
 * Главная страница `/schedule/` (диспетчеризация по роли, как в MainServlet)
 * и страницы студента: расписание и график оценок.
 */
@Controller
class StudentController(
    private val authService: AuthService,
    private val scheduleService: ScheduleService,
) {

    @GetMapping("/schedule/")
    fun main(session: HttpSession, model: Model): String {
        val worker = authService.findWorker(session.getAttribute("name") as String)
        return when (worker) {
            is Student -> {
                model.addAttribute("worker", worker)
                model.addAttribute("groupId", worker.group?.id)
                model.addAttribute("subjects", scheduleService.getStudentSchedule(worker))
                "student"
            }
            is Teacher -> {
                model.addAttribute("worker", worker)
                model.addAttribute("subjects", scheduleService.getTeacherSubjects(worker.un))
                "teacher_main"
            }
            is Admin -> "redirect:/schedule/admin/"
            else -> {
                session.invalidate()
                "redirect:/schedule/login/"
            }
        }
    }

    /** График оценок студента по выбранному предмету. */
    @GetMapping("/schedule/graphic/{groupId}/")
    fun graphics(
        @PathVariable groupId: Int,
        @RequestParam("SubjectName") subjectName: String,
        session: HttpSession,
        model: Model,
    ): String {
        val worker = authService.findWorker(session.getAttribute("name") as String)
        if (worker !is Student) return "no_permission"
        val subject = scheduleService.getStudentSchedule(worker).find { it.subjectName == subjectName }
            ?: return "redirect:/schedule/"
        model.addAttribute("worker", worker)
        model.addAttribute("subject", subject)
        return "graphics"
    }
}
