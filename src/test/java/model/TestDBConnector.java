package model;

import myDB.supportClasses.Group;
import myDB.supportClasses.dataNode;
import myDB.workers.Teacher;
import myDB.workers.Worker;
import org.hamcrest.MatcherAssert;
import org.junit.*;
import org.junit.rules.ExpectedException;

import myDB.myDataBase;
import myDB.workers.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

class TestDBConnector {

    private DBConnector connector;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()throws Exception{
//        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_psw.txt";
//        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_ns.txt";
//        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/group_list.txt";
//        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/subjects_data.xml";
//        connector =  DBConnector.getInstanceWithParameters(pwdPath, namesPath, groupsPath, mdsPath);
    }

    @Test
    void testGetSubjectsForTeachersWithRigthName() {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/test/resources/subjects_data.xml";
        connector = DBConnector.getInstanceWithParameters(pwdPath, namesPath, groupsPath, mdsPath);
        boolean isFindedTeacher = true;
        Worker w = new Teacher("te1", "Maureen", "Garcia");
        HashMap<String, ArrayList<Group>> teacherSubjects = connector.getSubjectsForTeachers(w);
        myDataBase mDB = connector.getDBLink();
        int coincidence = 0;
        for (dataNode dn : mDB.getNodes()) {
            for (Map.Entry<String, ArrayList<Group>> entry : teacherSubjects.entrySet()) {
                if (entry.getKey().equals(dn.getSubjectName()) && dn.getTeacherId().equals("te1")) {
                    coincidence++;
                    break;
                }
            }
        }
        if (coincidence != teacherSubjects.size()) {
            isFindedTeacher = false;
            MatcherAssert.assertThat(isFindedTeacher, is(true));
            //assertThat(isFindedTeacher,is(true));
        }
    }

    @Test
    void correctStudent() {
    }

    @Test
    void makeNewLesson() {
    }

    @Test
    void deleteLesson() {
    }

    @Test
    void removeWorker() {
    }

    @Test
    void addWorker() {
        Student studentWithCorrectName = new Student("st8","Kim", "Seidu");
        Student studentWithIncorrectName = new Student("st9","Kim Jo", "Sei Du");

    }

    @Test
    void addGroup() {
    }

    @Test
    void removeGroup() {
    }

    @Test
    void addSubject() {
    }

    @Test
    void removeSubject() {
    }
}