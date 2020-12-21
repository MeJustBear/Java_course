package model;

import java.util.ArrayList;

public class Subject {
    String teacherName;
    ArrayList<String> lessonName;
    ArrayList<String> lessonDate;
    ArrayList<Integer> mark;
    ArrayList<String> comment;

    public void setComment(ArrayList<String> comment) {
        this.comment = comment;
    }

    public ArrayList<Integer> getMark() {
        return mark;
    }

    public ArrayList<String> getComment() {
        return comment;
    }

    public ArrayList<String> getLessonDate() {
        return lessonDate;
    }

    public ArrayList<String> getLessonName() {
        return lessonName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setLessonDate(ArrayList<String> lessonDate) {
        this.lessonDate = lessonDate;
    }

    public void setLessonName(ArrayList<String> lessonName) {
        this.lessonName = lessonName;
    }

    public void setMark(ArrayList<Integer> mark) {
        this.mark = mark;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
