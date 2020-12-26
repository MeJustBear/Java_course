package myDB;

import model.DBConnector;
import myDB.supportClasses.Group;
import myDB.supportClasses.dataNode;
import myDB.supportClasses.lesson;
import myDB.workers.Teacher;
import myDB.workers.Worker;
import org.hamcrest.MatcherAssert;
import org.junit.*;
import org.junit.rules.ExpectedException;

import myDB.myDataBase;
import myDB.workers.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

class myDataBaseTest {

    private myDataBase dataBase;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init(){

    }


//    @Before
//    public void setUp() throws Exception {
//        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_psw.txt";
//        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_ns.txt";
//        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/group_list.txt";
//        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/subjects_data.xml";
//        dataBase = new myDataBase(pwdPath,namesPath,groupsPath,mdsPath);
//    }

    @Test
    void removeWorker_Student() throws ParserConfigurationException, SAXException, IOException {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/subjects_data.xml";
        dataBase = new myDataBase(pwdPath,namesPath,groupsPath,mdsPath);
        Student student = new Student("st1","Rose","Abott");
        student.setGroup(1);
        dataBase.removeWorker(student);
        boolean testParam = true;
        if(dataBase.getPwdBase().get("st1") != null){
            testParam = false;
        }
        HashMap<Integer,Group> group =  dataBase.getGroups();
        if(group.get((Integer)1).contains(student)){
            testParam = false;
        }
        List<Worker> studs = dataBase.getWorkers().get("Student");
        for(Worker worker : studs){
            if(worker.getUn().equals(student.getUn())){
                testParam = false;
                break;
            }
        }
        for(dataNode dn : dataBase.getNodes()){
            ArrayList<lesson> lessonArrayList = dn.getLessons();
            for(lesson l : lessonArrayList){
                if(l.getResults().get("st1") != null){
                    testParam = false;
                    break;
                }
            }
        }
        assertThat(testParam, is(true));
    }

    @Test
    void addWorker_Teacher() throws ParserConfigurationException, SAXException, IOException {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/subjects_data.xml";
        dataBase = new myDataBase(pwdPath,namesPath,groupsPath,mdsPath);
        dataBase.addWorker(new Teacher("te3","Paola", "Menendez"));
        boolean testResult = true;
        if(!dataBase.getPwdBase().containsKey("te3")){
            testResult = false;
            MatcherAssert.assertThat(testResult, is(true));
        }
        for(ArrayList<Worker> workerArrayList : dataBase.getWorkers().get("Teacher")){
            for(Worker w : workerArrayList){
                if(w.getUn().equals("te3")){
                    testResult = true;
                    break;
                }
            }
        }
        MatcherAssert.assertThat(testResult, is(true));
    }

    @Test
    void removeGroup() throws ParserConfigurationException, SAXException, IOException {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/subjects_data.xml";
        dataBase = new myDataBase(pwdPath,namesPath,groupsPath,mdsPath);
        boolean testResult = true;
        Group group = dataBase.getGroup(1);
        dataBase.removeGroup(group);
        for(Student st : group.getList()){
            if(dataBase.getPwdBase().get(st.getUn()) != null){
                testResult = false;
                MatcherAssert.assertThat(testResult, is(true));
                break;
            }
        }
        if(dataBase.getGroups().get(group.getGroupId()) != null){
            testResult = false;
            MatcherAssert.assertThat(testResult, is(true));
        }
        for(dataNode dn : dataBase.getNodes()){
            if(dn.getGroupId() == group.getGroupId()){
                testResult = false;
                MatcherAssert.assertThat(testResult, is(true));
            }
        }
        for(Worker w : dataBase.getWorkers().get("Student")){
            Student st = (Student) w;
            if(st.getGroup() == group.getGroupId()) {
                testResult = false;
                MatcherAssert.assertThat(testResult, is(true));
            }
        }
        MatcherAssert.assertThat(testResult, is(true));
    }
//
//    @Test
//    void addSubject() {
//
//    }
}