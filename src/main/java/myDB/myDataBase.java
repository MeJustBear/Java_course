package myDB;

import myDB.supportClasses.lesson;
import org.xml.sax.SAXException;
import myDB.supportClasses.Group;
import myDB.supportClasses.dataNode;
import myDB.supportClasses.parser;
import myDB.workers.Worker;
import myDB.workers.Admin;
import myDB.workers.Student;
import myDB.workers.Teacher;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class myDataBase {

    private HashMap<String, String> pwdBase = new HashMap<>();
    private HashMap<String, List<Worker>> workers = new HashMap<>();
    private HashMap<Integer, Group> groups = new HashMap<>();
    private ArrayList<dataNode> dataNodes;
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

    public void save() throws IOException {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/subjects_data.xml";

        savePasswords(pwdPath);
        saveNames(namesPath);
        saveGroups(groupsPath);
        saveMDS(mdsPath);
    }

    private void saveGroups(String path) throws IOException {
        //FileWriter fileWriter = new FileWriter(path);
        PrintStream printStream = new PrintStream(path);
        for(Map.Entry<Integer,Group> entry : groups.entrySet()){
            printStream.print(entry.getKey().toString() + " ");
            for(Student st : entry.getValue().getList()){
                String s = new String(entry.getKey().toString() + " " + st.getName() + " " + st.getSurname());
                printStream.print(st.getUn() + " ");
            }
            printStream.println();
        }
        printStream.close();
    }

    private void saveNames(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(Map.Entry<String,List<Worker>> entry : workers.entrySet()){
            List<Worker> curWorkers = entry.getValue();
            for(Worker w : curWorkers){
                printWriter.println(w.getUn() + " " + w.getName() + " " + w.getSurname());
            }
        }
        printWriter.close();
    }

    private void saveMDS(String mdsPath) throws IOException {
        FileWriter fileWriter = new FileWriter(mdsPath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <Data>");
        for(dataNode dn : dataNodes){
            printWriter.println("<Subject>");
            printWriter.println("<Name>" + dn.getSubjectName() + "</Name>\n<Teacher>" + dn.getTeacherId() + "</Teacher>\n<Group>" + dn.getGroupId() + "</Group>\n<Lessons>");
            ArrayList<lesson> lessons = dn.getLessons();
            for(lesson l : lessons){
                printWriter.println("<Lesson>\n<Name> " + l.getName() + "</Name>\n<Date> " + l.getLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                        "</Date>\n<Students>");
                HashMap<String, lesson.lessonNode> lessonMap = l.getResults();
                for(Map.Entry<String,lesson.lessonNode> curStud : lessonMap.entrySet()){
                    printWriter.println("<Student>\n<Name>" + curStud.getKey() + "</Name>\n<Mark>" + curStud.getValue().getMark() +
                            "</Mark>\n<Comment>" + curStud.getValue().getComment()
                            + "</Comment>\n</Student>");
                }
                printWriter.println("</Students>\n</Lesson>");
            }
            printWriter.print("</Lessons></Subject>\n");
        }
        printWriter.println("</Data>");
        printWriter.close();
    }

    private void savePasswords(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(Map.Entry<String,String> map : pwdBase.entrySet()){
            printWriter.println(map.getKey() + " " + map.getValue());
        }
        printWriter.close();
    }

    public Group getGroup(int groupId){
        return groups.get(groupId);
    }

    public ArrayList<dataNode> getNodes() {
        return dataNodes;
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

    public lesson getLesson(String teacherId, String subj, String les, int gr) {
        for(dataNode dn : dataNodes){
            if(dn.getSubjectName().equals(subj) && dn.getTeacherId().equals(teacherId) && dn.getGroupId() == gr){
                for(lesson l : dn.getLessons()){
                    if(l.getName().equals(les)){
                        return l;
                    }
                }
            }
        }
        return null;
    }
}
