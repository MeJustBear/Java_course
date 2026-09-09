package com.course.schedule.web

import com.course.schedule.service.AuthService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/** Вход и выход (Login/LogoutServlet в исходном проекте). */
@Controller
class AuthController(private val authService: AuthService) {

    @GetMapping("/")
    fun index(): String = "redirect:/schedule/"

    @GetMapping("/schedule/login/")
    fun loginPage(session: HttpSession): String =
        if (session.getAttribute("name") != null) "redirect:/schedule/" else "login"

    @PostMapping("/schedule/login/")
    fun login(
        @RequestParam uname: String,
        @RequestParam psw: String,
        session: HttpSession,
    ): String {
        return if (authService.authenticate(uname, psw)) {
            session.setAttribute("name", uname)
            "redirect:/schedule/"
        } else {
            "redirect:/schedule/login/"
        }
    }

    @GetMapping("/schedule/logout/")
    fun logout(session: HttpSession): String {
        session.invalidate()
        return "redirect:/schedule/login/"
    }
}
