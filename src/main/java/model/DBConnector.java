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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public Group getGroup(int groupId) {
        return dataBase.getGroup(groupId);
    }

    public HashMap<String, ArrayList<Group>> getSubjectsForTeachers(Worker worker) {
        Teacher teacher = (Teacher) worker;
        HashMap<String, ArrayList<Group>> map = new HashMap<>();
        TreeSet<String> subjNames = new TreeSet<>();
        ArrayList<dataNode> nodes = dataBase.getNodes();
        String subjName = null;
        for (dataNode dn : nodes) {
            if (dn.getTeacherId().equals(teacher.getUn())) {
                subjName = dn.getSubjectName();
                if (!subjNames.contains(subjName)) {
                    map.put(subjName, new ArrayList<>());
                    map.get(subjName).add(dataBase.getGroup(dn.getGroupId()));

                } else {
                    map.get(subjName).add(dataBase.getGroup(dn.getGroupId()));
                }
                subjNames.add(subjName);
            }
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

    public ArrayList<Subject> getContcreteSubjs(Group group, Worker w, String subName) {
        Teacher teacher = (Teacher) w;
        ArrayList<dataNode> nodes = dataBase.getNodes();
        ArrayList<Subject> subjects = new ArrayList<>();
        Subject subj = new Subject();
        for (dataNode dn : nodes) {
            if (dn.getGroupId() == group.getGroupId() && dn.getTeacherId().equals(teacher.getUn()) && dn.getSubjectName().equals(subName)) {
                subj.setSubjectName(dn.getSubjectName());
                ArrayList<lesson> lessons = dn.getLessons();
                for (lesson les : lessons) {
                    subj.addLessonName(les.getName());
                    subj.addLessonDate(les.getDate());
                }
                subjects.add(subj);
            }
        }
        return subjects;
    }

    public lesson getLesson(String teacherId, String subj, String les, int gr) {
        lesson res = dataBase.getLesson(teacherId,subj,les,gr);
        return res;
    }

    public void correctStudent(String stId, String comment, int mark, String uri) {
        String subject = (uri.split("/"))[3].replaceAll("_"," ");
        String les = (uri.split("/"))[4].replaceAll("_"," ");
        Student student = (Student) this.getWorker(stId);
        ArrayList<dataNode> nodes = dataBase.getNodes();
        for(dataNode dn : nodes){
            if(dn.getSubjectName().equals(subject) && dn.getGroupId() == student.getGroup()){
                ArrayList<lesson> lsons = dn.getLessons();
                for(lesson l : lsons){
                    if(l.getName().equals(les)){
                        HashMap<String, lesson.lessonNode> map = l.getResults();
                        lesson.lessonNode ln = map.get(stId);
                        ln.setComment(comment);
                        ln.setMark(mark);
                        return;
                    }
                }
            }
        }
    }

    public void makeNewLesson(String uri, String subject, String date, String subjName) {
        String[] str = date.split("-");
        StringBuilder sb =  new StringBuilder();
        sb.append(str[2] + "-" + str[1] + "-" + str[0]);
        date = sb.toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date,formatter);
        str = uri.split("/");
        int groupId = Integer.parseInt(str[2].substring(2));
        ArrayList<dataNode> dataNodes = dataBase.getNodes();
        for(dataNode dn : dataNodes){
            if(dn.getSubjectName().equals(subject) && dn.getGroupId() == groupId){
                dn.pushBackLesson(subjName,localDate);
                return;
            }
        }
    }

    public void deleteLesson(String uri, String subject, String subjName) {
        String[] str = uri.split("/");
        int groupId = Integer.parseInt(str[2].substring(2));
        ArrayList<dataNode> dataNodes = dataBase.getNodes();
        for(dataNode dn : dataNodes){
            if(dn.getSubjectName().equals(subject) && dn.getGroupId() == groupId){
                ArrayList<lesson> dataNodeLesson = dn.getLessons();
                for(int i = 0; i < dataNodeLesson.size(); i++) {
                    if(dataNodeLesson.get(i).getName().equals(subjName)){
                        dataNodeLesson.remove(i);
                        return;
                    }
                }
            }
        }
    }

    public void saveDB() {
        dataBase.save();
    }
}
