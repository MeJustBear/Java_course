package com.course.schedule.domain

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Иерархия пользователей исходного проекта (Worker/Student/Teacher/Admin),
 * отображённая на одну таблицу с дискриминатором роли.
 * Первичный ключ — логин `un` с ролевым префиксом (st/te/ad), как в оригинале.
 */
@Entity
@Table(name = "worker")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
abstract class Worker(
    @Id
    @Column(name = "un")
    var un: String = "",

    var name: String = "",

    var surname: String = "",

    var password: String = "",
) {
    val fullName: String
        get() = "$name $surname"
}

@Entity
@DiscriminatorValue("STUDENT")
class Student(
    un: String = "",
    name: String = "",
    surname: String = "",
    password: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    var group: StudyGroup? = null,
) : Worker(un, name, surname, password)

@Entity
@DiscriminatorValue("TEACHER")
class Teacher(
    un: String = "",
    name: String = "",
    surname: String = "",
    password: String = "",
) : Worker(un, name, surname, password)

@Entity
@DiscriminatorValue("ADMIN")
class Admin(
    un: String = "",
    name: String = "",
    surname: String = "",
    password: String = "",
) : Worker(un, name, surname, password)
