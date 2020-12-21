package myDB;

import org.xml.sax.SAXException;
import myDB.supportClasses.Group;
import myDB.supportClasses.dataNode;
import myDB.supportClasses.parser;
import myDB.workers.Worker;
import myDB.workers.Admin;
import myDB.workers.Student;
import myDB.workers.Teacher;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class myDataBase {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        myDataBase mDB = new myDataBase();
        for (int i = 0; i < 10; i++) {
            i += 1;
            i -= 1;
        }
    }

    private HashMap<String, String> pwdBase = new HashMap<>();
    private HashMap<String, List<Worker>> workers = new HashMap<>();
    private HashMap<Integer, Group> groups = new HashMap<>();
    private ArrayList<dataNode> dataNodes;//= new ArrayList<>();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public myDataBase() throws IOException, ParserConfigurationException, SAXException {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/subjects_data.xml";

        readPasswords(pwdPath);
        readNames(namesPath);
        readGroups(groupsPath);
        readMDS(mdsPath);
    }

    public boolean findPas(String name, String password){
        if(pwdBase.get(name).equals(password)){
            return true;
        }else{
            return false;
        }
    }

    private void readMDS(String mdsPath) throws IOException, ParserConfigurationException, SAXException {
        dataNodes = parser.parseXML(mdsPath,format);
    }


    private void readGroups(String groupsPath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(groupsPath));//"../resource/group_list.txt");
        List<Worker> studs = workers.get("Student");
        while (scanner.hasNext()) {
            String tmp = scanner.nextLine();
            String[] subStr = tmp.split(" ");
            Integer groupId = Integer.parseInt(subStr[0]);
            Group currentGroup = new Group(groupId.intValue());
            for (int i = 1; i < subStr.length; i++) {
                Student st = null;
                for (Worker w : studs) {
                    if (w.getUn().equals(subStr[i])) {
                        st = (Student) w;
                        st.setGroup(groupId);
                        break;

                    }
                }
                if (st != null) {
                    currentGroup.addStudent(st);
                }
            }
            groups.put(currentGroup.getGroupId(), currentGroup);
        }
    }


    void readPasswords(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNext()) {
            String tmp = scanner.nextLine();
            String[] subStr = tmp.split(" ");
            pwdBase.put(subStr[0], subStr[1]);
        }
    }

    void readNames(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        List<Worker> students = new ArrayList<>();
        List<Worker> teachers = new ArrayList<>();
        List<Worker> admins = new ArrayList<>();
        while (scanner.hasNext()) {
            String tmp = scanner.nextLine();
            String[] subStr = tmp.split(" ");
            if (subStr[0].startsWith("st")) {
                students.add(new Student(subStr[0], subStr[1], subStr[2]));
            } else if (subStr[0].startsWith("te")) {
                teachers.add(new Teacher(subStr[0], subStr[1], subStr[2]));
            } else if (subStr[0].startsWith("ad")) {
                admins.add(new Admin(subStr[0], subStr[1], subStr[2]));
            }
        }
        workers.put("Student", students);
        workers.put("Teacher", teachers);
        workers.put("Admin", admins);
    }

    public HashMap<String, List<Worker>> getWorkers() {
        return workers;
    }
}
