package com.course.schedule.web

import com.course.schedule.domain.Admin
import com.course.schedule.service.AdminService
import com.course.schedule.service.AuthService
import com.course.schedule.service.Credentials
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * Администрирование: группы, студенты, преподаватели, предметы
 * (admin-ветка MainServlet + admin_main.jsp / admin_cur_group.jsp).
 * Формы отправляются GET-запросами, как в исходном проекте.
 */
@Controller
class AdminController(
    private val authService: AuthService,
    private val adminService: AdminService,
) {

    @GetMapping("/schedule/admin/")
    fun adminMain(session: HttpSession, model: Model): String {
        val admin = currentAdmin(session) ?: return "no_permission"
        model.addAttribute("worker", admin)
        model.addAttribute("groups", adminService.getGroups())
        model.addAttribute("teachers", adminService.getTeachers())
        model.addAttribute("teacherSubjects", adminService.getTeacherSubjectNames())
        model.addAttribute("nextGroupId", adminService.nextGroupId())
        return "admin_main"
    }

    @GetMapping("/schedule/find_group/{groupId}/")
    fun groupPage(@PathVariable groupId: Int, session: HttpSession, model: Model): String {
        val admin = currentAdmin(session) ?: return "no_permission"
        val students = adminService.getGroupStudents(groupId) ?: return "redirect:/schedule/admin/"
        model.addAttribute("worker", admin)
        model.addAttribute("groupId", groupId)
        model.addAttribute("students", students)
        return "admin_group"
    }

    @GetMapping("/schedule/add_group/")
    fun addGroup(@RequestParam groupId: Int, session: HttpSession): String {
        currentAdmin(session) ?: return "no_permission"
        adminService.addGroup(groupId)
        return "redirect:/schedule/admin/"
    }

    @GetMapping("/schedule/remove_group/")
    fun removeGroup(@RequestParam groupId: Int, session: HttpSession): String {
        currentAdmin(session) ?: return "no_permission"
        adminService.removeGroup(groupId)
        return "redirect:/schedule/admin/"
    }

    @GetMapping("/schedule/add_teacher/")
    fun addTeacher(
        @RequestParam("TeacherName") name: String,
        @RequestParam("TeacherSurName") surname: String,
        session: HttpSession,
        redirect: RedirectAttributes,
    ): String {
        currentAdmin(session) ?: return "no_permission"
        addCredentials(redirect, adminService.addTeacher(name.trim(), surname.trim()))
        return "redirect:/schedule/admin/"
    }

    @GetMapping("/schedule/remove_teacher/")
    fun removeTeacher(@RequestParam teacherId: String, session: HttpSession): String {
        currentAdmin(session) ?: return "no_permission"
        adminService.removeTeacher(teacherId)
        return "redirect:/schedule/admin/"
    }

    @GetMapping("/schedule/add_subject/")
    fun addSubject(
        @RequestParam("SubjectName") subjectName: String,
        @RequestParam("Teacher") teacherUn: String,
        @RequestParam("Group") groupId: Int,
        session: HttpSession,
    ): String {
        currentAdmin(session) ?: return "no_permission"
        adminService.assignSubject(subjectName.trim(), teacherUn, groupId)
        return "redirect:/schedule/admin/"
    }

    @GetMapping("/schedule/remove_subject/")
    fun removeSubject(
        @RequestParam teacherUn: String,
        @RequestParam subjectName: String,
        session: HttpSession,
    ): String {
        currentAdmin(session) ?: return "no_permission"
        adminService.removeSubject(teacherUn, subjectName)
        return "redirect:/schedule/admin/"
    }

    @GetMapping("/schedule/gr{groupId}/add_student/")
    fun addStudent(
        @PathVariable groupId: Int,
        @RequestParam("StudentName") name: String,
        @RequestParam("StudentSurName") surname: String,
        session: HttpSession,
        redirect: RedirectAttributes,
    ): String {
        currentAdmin(session) ?: return "no_permission"
        addCredentials(redirect, adminService.addStudent(groupId, name.trim(), surname.trim()))
        return "redirect:/schedule/find_group/$groupId/"
    }

    @GetMapping("/schedule/gr{groupId}/remove_student/")
    fun removeStudent(
        @PathVariable groupId: Int,
        @RequestParam("StudentName") studentUn: String,
        session: HttpSession,
    ): String {
        currentAdmin(session) ?: return "no_permission"
        adminService.removeStudent(studentUn)
        return "redirect:/schedule/find_group/$groupId/"
    }

    private fun addCredentials(redirect: RedirectAttributes, credentials: Credentials?) {
        if (credentials != null) {
            redirect.addFlashAttribute("newUsername", credentials.username)
            redirect.addFlashAttribute("newPassword", credentials.password)
        }
    }

    private fun currentAdmin(session: HttpSession): Admin? =
        authService.findWorker(session.getAttribute("name") as String) as? Admin
}
