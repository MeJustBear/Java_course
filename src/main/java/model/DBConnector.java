package model;

import myDB.*;
import myDB.supportClasses.Group;
import myDB.supportClasses.dataNode;
import myDB.supportClasses.lesson;
import myDB.workers.Student;
import myDB.workers.Teacher;
import myDB.workers.Worker;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class DBConnector {
    private static myDataBase dataBase;
    private static DBConnector instance;

    private DBConnector() {
        try {
            dataBase = new myDataBase();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static DBConnector getInstance() {

        if (instance == null) {
            instance = new DBConnector();
        }
        return instance;
    }

    public boolean checkUsernamePassword(String username, String password) {
        return dataBase.findPas(username, password);
    }

    public HashMap<String,ArrayList<Group>> getSubjectsForTeachers(Worker worker){
        Teacher teacher = (Teacher) worker;
        HashMap<String, ArrayList<Group>> map = new HashMap<>();
        TreeSet<String> subjNames = new TreeSet<>();
        ArrayList<dataNode> nodes = dataBase.getNodes();
        String subjName = null;
        for(dataNode dn : nodes ){
            if(dn.getTeacherId().equals(teacher.getUn())){
                subjName = dn.getSubjectName();
                if(!subjNames.contains(subjName)){
                    map.put(subjName,new ArrayList<>());
                    map.get(subjName).add(dataBase.getGroup(dn.getGroupId()));

                }else {
                    map.get(subjName).add(dataBase.getGroup(dn.getGroupId()));
                }
            }
            subjNames.add(subjName);
        }
        return map;
    }

    public HashMap<String, Subject> getSubjects(Object obj) {
        HashMap<String, Subject> map = new HashMap<>();
        String subjName = null;
        Student st = (Student) this.getWorker(obj);
        ArrayList<dataNode> mainData = dataBase.getNodes();
        for (dataNode dN : mainData) {
            if (dN.getGroupId() == st.getGroup()) {
                subjName = dN.getSubjectName();
                Subject curSubj = new Subject();
                ArrayList<String> lessonName = new ArrayList<>();
                ArrayList<String> lessonDate = new ArrayList<>();
                ArrayList<Integer> mark = new ArrayList<>();
                ArrayList<String> comment = new ArrayList<>();
                Teacher t = (Teacher) this.getWorker(dN.getTeacherId());
                curSubj.setTeacherName((t.getName() + " " + t.getSurname()));
                for (lesson les : dN.getLessons()) {
                    lesson.lessonNode lN = les.getNodes(st.getUn());
                    lessonName.add(les.getName());
                    lessonDate.add(les.getDate().toString());
                    mark.add(lN.getMark());
                    comment.add(lN.getComment());
                }
                curSubj.setComment(comment);
                curSubj.setLessonDate(lessonDate);
                curSubj.setLessonName(lessonName);
                curSubj.setMark(mark);
                map.put(subjName, curSubj);
            }
        }
        return map;
    }

    public String getFullName(Object obj) {
        String name = (String) obj;
        HashMap<String, List<Worker>> workers = dataBase.getWorkers();
        if (name.startsWith("st")) {
            List<Worker> workerList = workers.get("Student");
            for (Worker w : workerList) {
                if (w.getUn().equals(name)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(w.getName());
                    sb.append(" ");
                    sb.append(w.getSurname());
                    return sb.toString();
                }
            }
        }
        return null;
    }

    public Worker getWorker(Object obj) {
        String name = (String) obj;
        HashMap<String, List<Worker>> workers = dataBase.getWorkers();
        if (name.startsWith("st")) {
            List<Worker> workerList = workers.get("Student");
            for (Worker w : workerList) {
                if (w.getUn().equals(name)) {
                    return w;
                }
            }
        }
        if (name.startsWith("te")) {
            List<Worker> workerList = workers.get("Teacher");
            for (Worker w : workerList) {
                if (w.getUn().equals(name)) {
                    return w;
                }
            }
        }
        return null;
    }

}
