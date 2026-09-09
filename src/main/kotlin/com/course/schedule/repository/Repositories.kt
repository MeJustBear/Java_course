package com.course.schedule.repository

import com.course.schedule.domain.Lesson
import com.course.schedule.domain.LessonResult
import com.course.schedule.domain.Student
import com.course.schedule.domain.StudyGroup
import com.course.schedule.domain.SubjectAssignment
import com.course.schedule.domain.Teacher
import com.course.schedule.domain.Worker
import org.springframework.data.jpa.repository.JpaRepository

interface WorkerRepository : JpaRepository<Worker, String>

interface StudentRepository : JpaRepository<Student, String> {
    fun findByGroupId(groupId: Int): List<Student>
}

interface TeacherRepository : JpaRepository<Teacher, String>

interface StudyGroupRepository : JpaRepository<StudyGroup, Int>

interface SubjectAssignmentRepository : JpaRepository<SubjectAssignment, Long> {
    fun findByGroupId(groupId: Int): List<SubjectAssignment>
    fun findByTeacherUn(teacherUn: String): List<SubjectAssignment>
    fun findFirstBySubjectNameAndGroupId(subjectName: String, groupId: Int): SubjectAssignment?
    fun findFirstByTeacherUnAndSubjectNameAndGroupId(
        teacherUn: String,
        subjectName: String,
        groupId: Int,
    ): SubjectAssignment?
    fun findFirstByTeacherUnAndSubjectName(teacherUn: String, subjectName: String): SubjectAssignment?
    fun deleteByTeacherUn(teacherUn: String)
    fun deleteByGroupId(groupId: Int)
}

interface LessonRepository : JpaRepository<Lesson, Long>

interface LessonResultRepository : JpaRepository<LessonResult, Long> {
    fun deleteByStudentUn(studentUn: String)
}
