package myDB;

import myDB.supportClasses.Group;
import myDB.supportClasses.MDNode;
import myDB.workers.Worker;
import myDB.workers.Admin;
import myDB.workers.Student;
import myDB.workers.Teacher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class myDataBase {

//    public static void main(String[] args) throws FileNotFoundException {
//        myDataBase mDB = new myDataBase();
//        for (int i = 0; i < 10; i++) {
//            i += 1;
//            i -= 1;
//        }
//    }

    private HashMap<String, String> pwdBase = new HashMap<>();
    private HashMap<String, List<Worker>> workers = new HashMap<>();
    private HashMap<Integer, Group> groups = new HashMap<>();
    private ArrayList<MDNode> mdNodes = new ArrayList<>();

    public myDataBase() throws FileNotFoundException {
        String pwdPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/un_psw.txt";
        String namesPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/un_ns.txt";
        String groupsPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/group_list.txt";
        String mdsPath = "/home/pavel/IdeaProjects/course/course_v0/src/main/webapp/resource/group_list.txt";

        readPasswords(pwdPath);
        readNames(namesPath);
        readGroups(groupsPath);
        readMDS(mdsPath);
    }

    private void readMDS(String mdsPath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(mdsPath));
        String tmp = scanner.nextLine();
        while (scanner.hasNext()) {
            if(!tmp.equals("newmds")){
                break;
            }
            MDNode node = new MDNode();
            tmp = scanner.nextLine();
            String[] subStr = tmp.split(" ");
            List<Worker> teachs = workers.get("Teacher");
            for (Worker w : teachs) {
                if (w.getUn().equals(subStr[1])) {
                    node.setTeacher((Teacher) w);
                    break;
                }
            }
            tmp = scanner.nextLine();
            tmp = tmp.substring(8, tmp.length());
            node.setSubject(tmp);
            tmp = scanner.nextLine();
            tmp = tmp.substring(6, tmp.length());
            int check = Integer.parseInt(tmp.trim());
            node.setGroup(groups.get(check));
            tmp = scanner.nextLine();
            tmp = tmp.substring(7,tmp.length());
            node.setLessonName(tmp);
            tmp = scanner.nextLine();
            tmp = tmp.substring(5,tmp.length());
            node.setLessonDate(tmp);
            tmp = scanner.nextLine();
            if(tmp.startsWith("service info")){
                tmp = scanner.nextLine();
                String comment = null;
                Student st = null;
                int mark = 0;
                while(!tmp.equals("newmds")){
                    subStr = tmp.split(" ");
                    if(subStr[0].equals("name:")){
                        tmp = tmp.substring(5,tmp.length()).trim();
                        List<Worker> studs = workers.get("Student");
                        for(Worker w: studs){
                            if(w.getUn().equals(tmp)){
                                st = (Student) w;
                                break;
                            }
                        }
                        tmp = scanner.nextLine();
                        tmp = tmp.substring(5,tmp.length());
                        mark = Integer.parseInt(tmp.trim());
                        tmp = scanner.nextLine();
                        subStr = tmp.split(" ");
                        if(subStr.length > 1){
                            tmp = tmp.substring(9,tmp.length());
                        }
                        comment = tmp;
                    }
                    node.addStudentMD(st,mark,comment);
                    tmp = scanner.nextLine();
                }
            }
        }
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

}
