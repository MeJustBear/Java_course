package myDB.supportClasses;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class lesson {

    public lesson(String subjName, LocalDate localDate) {
        name = subjName;
        date = localDate;
    }

    public class lessonNode{
        int mark = 0;
        String comment = null;

        public lessonNode(){
            this.mark = 0;
            this.comment = null;
        }

        public lessonNode(int mark, String comment){
            this.mark = mark;
            this.comment = comment;
        }

        public void setMark(int mark) {
            this.mark = mark;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getMark() {
            return mark;
        }

        public String getComment() {
            return comment;
        }
    }

    String name = null;
    LocalDate date = null;
    HashMap<String,lessonNode> results = new HashMap<>();

    public Set<String> getstudentsList(){
        return results.keySet();
    }

    public lessonNode getNodes(String studentId){
        return results.get(studentId);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResults(HashMap<String, lessonNode> results) {
        this.results = results;
    }

    public lesson(){
        this.date = date;
        this.name = name;
    }

    public void addNodeToMap(String student,int mark, String comment ){
        results.put(student,new lessonNode(mark,comment));
    }

    public String getName() {
        return name;
    }

    public HashMap<String, lessonNode> getResults() {
        return results;
    }

    public LocalDate getLocalDate(){
        return date;
    }

    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public lessonNode getEmptyNode(){
        return new lessonNode();
    }
}
