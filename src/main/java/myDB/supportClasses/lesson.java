package myDB.supportClasses;

import java.time.LocalDate;
import java.util.HashMap;

public class lesson {

    public class lessonNode{
        int mark;
        String comment;

        public lessonNode(int mark, String comment){
            this.mark = mark;
            this.comment = comment;
        }

    }

    String name = null;
    LocalDate date = null;
    HashMap<String,lessonNode> results = new HashMap<>();

    public lesson(){

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

    public lesson(String name, LocalDate date){
        this.date = date;
        this.name = name;
    }

    public void addNodeToMap(String student,int mark, String comment ){
        results.put(student,new lessonNode(mark,comment));
    }

}
