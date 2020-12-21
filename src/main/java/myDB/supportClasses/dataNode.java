package myDB.supportClasses;

import java.util.ArrayList;

public class dataNode {
    String subjectName = null;
    String teacherId = null;
    int groupId = 0;
    ArrayList<lesson> lessons = null;

    public dataNode(String subjectName, String teacherId, int groupId, ArrayList<lesson> lessons){
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.groupId = groupId;
        this.lessons = lessons;
    }

    public dataNode(){

    }

    public int getGroupId() {
        return groupId;
    }

    public ArrayList<lesson> getLessons() {
        return lessons;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setLessons(ArrayList<lesson> lessons) {
        this.lessons = lessons;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
