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
        setTeacherSubjects();

    }

    public int getNextId(Worker w){
        if(w instanceof Student) {
            List<Worker> workerList = workers.get("Student");
            return workerList.size() + 1;
        }
        return 0;
    }

    public void removeWorker(Worker curWorker){
        if(curWorker instanceof Student){
            Student student = (Student) curWorker;
            for(dataNode dn : dataNodes){
                if(dn.getGroupId() == student.getGroup()){
                    for(lesson l : dn.getLessons()){
                        l.removeStudent(student);
                        break;
                    }
                }
            }
            Group stGroup = groups.get(student.getGroup());
            stGroup.removeStudent(student);
            List<Worker> workerList = workers.get("Student");
            for(int i =0; i < workerList.size(); i++){
                if(workerList.get(i).getUn().equals(student.getUn())){
                    workerList.remove(i);
                    break;
                }
            }
            pwdBase.remove(student.getUn());
        }


    }

    private void setTeacherSubjects() {
        for(Worker worker : workers.get("Teacher")){
            Teacher teacher = (Teacher) worker;
            ArrayList<String> TeacherSubjects = new ArrayList<>();
            for(dataNode dn : dataNodes){
                if(dn.getTeacherId().equals(teacher.getUn())){
                    TeacherSubjects.add(dn.getSubjectName());
                }
            }
            teacher.setSubjects(TeacherSubjects);
        }
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
                    if(curStud.getKey() == null)
                        continue;
                    String comment = curStud.getValue().getComment();
                    if(comment == null)
                        comment = "";
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

    public HashMap<Integer, Group> getGroups() {
        return  groups;
    }

    public ArrayList<String> addWorker(Worker worker) {
        ArrayList<String> list = new ArrayList<>();
        if(worker instanceof Student){
            Student student = (Student) worker;
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 4;
            Random random = new Random();

            String password = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            pwdBase.put(worker.getUn(),password);

            list.add(worker.getUn());
            list.add(password);
            workers.get("Student").add(worker);
            groups.get((student.getGroup())).addStudent(student);
            for(dataNode dn : dataNodes){
                if(dn.getGroupId() == student.getGroup()){
                    for(lesson l : dn.getLessons()){
                        l.addNodeToMap(student.getUn(),0,new String(""));
                    }
                }
            }
        }
        return list;
    }
}
