package model;

import java.util.ArrayList;

public class Subject {
    String teacherName;
    String subjectName = null;
    ArrayList<String> lessonName;
    ArrayList<String> lessonDate;
    ArrayList<Integer> mark;
    ArrayList<String> comment;

    public Subject(){
        ArrayList<String> lessonName = new ArrayList<>();
        ArrayList<String> lessonDate = new ArrayList<>();
    }

    public void addLessonName(String name){
        if(lessonName == null){
            lessonName = new ArrayList<>();
        }
        lessonName.add(name);
    }

    public void addLessonDate(String date){
        if(lessonDate == null){
            lessonDate = new ArrayList<>();
        }
        lessonDate.add(date);
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }

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
