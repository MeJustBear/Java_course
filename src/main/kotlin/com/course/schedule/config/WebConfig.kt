package com.course.schedule.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Все страницы раздела `/schedule/` требуют залогиненной сессии (атрибут "name"),
 * как в исходном MainServlet: без сессии — редирект на форму логина.
 */
@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoginInterceptor())
            .addPathPatterns("/schedule/**")
            .excludePathPatterns("/schedule/login/**")
    }
}

class LoginInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session = request.getSession(false)
        if (session?.getAttribute("name") == null) {
            response.sendRedirect("/schedule/login/")
            return false
        }
        return true
    }
}
