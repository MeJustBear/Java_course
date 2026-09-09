package com.course.schedule.service

import com.course.schedule.domain.Worker
import com.course.schedule.repository.WorkerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** Проверка логина/пароля (checkUsernamePassword из исходного DBConnector). */
@Service
class AuthService(private val workerRepository: WorkerRepository) {

    @Transactional(readOnly = true)
    fun authenticate(username: String, password: String): Boolean {
        val worker = workerRepository.findById(username).orElse(null) ?: return false
        return worker.password == password
    }

    @Transactional(readOnly = true)
    fun findWorker(username: String): Worker? = workerRepository.findById(username).orElse(null)
}
