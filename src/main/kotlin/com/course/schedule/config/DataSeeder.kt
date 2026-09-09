package com.course.schedule.config

import com.course.schedule.domain.Admin
import com.course.schedule.domain.Lesson
import com.course.schedule.domain.LessonResult
import com.course.schedule.domain.Student
import com.course.schedule.domain.StudyGroup
import com.course.schedule.domain.SubjectAssignment
import com.course.schedule.domain.Teacher
import com.course.schedule.repository.StudyGroupRepository
import com.course.schedule.repository.SubjectAssignmentRepository
import com.course.schedule.repository.WorkerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDate

/**
 * Заполняет пустую БД данными исходного проекта
 * (un_psw.txt, un_ns.txt, group_list.txt, subjects_data.xml).
 */
@Configuration
class DataSeeder {

    @Bean
    fun seedRunner(
        workerRepository: WorkerRepository,
        groupRepository: StudyGroupRepository,
        assignmentRepository: SubjectAssignmentRepository,
        transactionTemplate: TransactionTemplate,
    ) = CommandLineRunner {
        transactionTemplate.executeWithoutResult {
            if (workerRepository.count() > 0) return@executeWithoutResult

            val group1 = groupRepository.save(StudyGroup(id = 1))
            val group2 = groupRepository.save(StudyGroup(id = 2))

            val st1 = Student("st1", "Rose", "Abott", "123", group1)
            val st2 = Student("st2", "Paula", "Boone", "123", group2)
            val st3 = Student("st3", "Lindsay", "Gill", "123", group1)
            val st4 = Student("st4", "Pola", "Cowsky", "123", group2)
            val st6 = Student("st6", "Elen", "Ty", "sVrC", group2)
            val te1 = Teacher("te1", "Maureen", "Garcia", "123")
            val te2 = Teacher("te2", "Dixie", "Blake", "123")
            val ad1 = Admin("ad1", "Lila", "Cooper", "123")
            val ad2 = Admin("ad2", "Tara", "Ramsey", "123")
            workerRepository.saveAll(listOf(st1, st2, st3, st4, st6, te1, te2, ad1, ad2))

            fun lessonOf(
                assignment: SubjectAssignment,
                name: String,
                date: LocalDate,
                marks: List<Triple<Student, Int, String?>>,
            ): Lesson {
                val lesson = Lesson(name = name, date = date, assignment = assignment)
                marks.forEach { (student, mark, comment) ->
                    lesson.results.add(
                        LessonResult(lesson = lesson, student = student, mark = mark, comment = comment),
                    )
                }
                return lesson
            }

            val spirituality = SubjectAssignment(subjectName = "Spirituality", teacher = te1, group = group1)
            spirituality.lessons += lessonOf(
                spirituality, "practice 1", LocalDate.of(2020, 1, 18),
                listOf(Triple(st1, 4, null), Triple(st3, 4, null)),
            )
            spirituality.lessons += lessonOf(
                spirituality, "practice 2", LocalDate.of(2020, 1, 10),
                listOf(Triple(st1, 5, null), Triple(st3, 3, null)),
            )
            spirituality.lessons += lessonOf(
                spirituality, "Les", LocalDate.of(2020, 12, 10),
                listOf(Triple(st1, 5, null), Triple(st3, 3, null)),
            )

            val spaceTravel = SubjectAssignment(subjectName = "Space Travel", teacher = te1, group = group1)
            spaceTravel.lessons += lessonOf(
                spaceTravel, "practice 1", LocalDate.of(2020, 1, 1),
                listOf(Triple(st1, 5, null), Triple(st3, 4, null)),
            )
            spaceTravel.lessons += lessonOf(
                spaceTravel, "practice 2", LocalDate.of(2020, 1, 10),
                listOf(Triple(st1, 5, null), Triple(st3, 3, null)),
            )

            val alienCrafting = SubjectAssignment(subjectName = "Alien Crafting", teacher = te1, group = group2)
            alienCrafting.lessons += lessonOf(
                alienCrafting, "Some", LocalDate.of(2020, 12, 19),
                listOf(Triple(st2, 4, null), Triple(st4, 5, null), Triple(st6, 0, null)),
            )
            alienCrafting.lessons += lessonOf(
                alienCrafting, "Lesson", LocalDate.of(2020, 12, 22),
                listOf(Triple(st2, 4, "qwertty"), Triple(st4, 0, null), Triple(st6, 0, null)),
            )

            val gardening = SubjectAssignment(subjectName = "Gardening", teacher = te2, group = group2)
            gardening.lessons += lessonOf(
                gardening, "Remote lesson", LocalDate.of(2020, 12, 17),
                listOf(Triple(st2, 5, null), Triple(st4, 0, null), Triple(st6, 0, null)),
            )

            assignmentRepository.saveAll(listOf(spirituality, spaceTravel, alienCrafting, gardening))
        }
    }
}
