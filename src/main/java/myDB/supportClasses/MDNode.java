package myDB.supportClasses;

import myDB.workers.Student;
import myDB.workers.Teacher;

import java.util.ArrayList;

public class MDNode {
    Teacher teach;
    String subject;
    Group group;
    String lessonName;
    String lessonDate;

    private class studentMD{
        Student student;
        int mark;
        String comment;

        studentMD(Student student, int mark, String comment){
            this.student = student;
            this.mark = mark;
            this.comment = comment;
        }
    }

    ArrayList<studentMD> posts = new ArrayList<>();

    public void setLessonDate(String lessonDate) {
        this.lessonDate = lessonDate;
    }

    public void setLessonName(String lessonName){
        this.lessonName = lessonName ;
    }

    public Teacher getTeacher(){
        return this.teach;
    }

    public void addStudentMD(Student student, int mark, String comment){
        this.posts.add(new studentMD(student,mark,comment));
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacher(Teacher teach) {
        this.teach = teach;
    }
}
