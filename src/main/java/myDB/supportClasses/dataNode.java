package myDB.supportClasses;

import myDB.workers.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

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

    public void pushBackLesson(String subjName, LocalDate localDate,Group group) {
        HashMap<String, lesson.lessonNode> map = new HashMap<>();
        lesson l = null;
        if(lessons.size() != 0) {
            Set<String> studNames = lessons.get(0).results.keySet();
            for (String name : studNames) {
                map.put(name, new lesson().getEmptyNode());
            }
        }else{
            for(Student st : group.getList()){
                map.put(st.getUn(), new lesson().getEmptyNode());
            }
        }
        l = new lesson(subjName, localDate);
        l.setResults(map);
        lessons.add(l);
    }
}
