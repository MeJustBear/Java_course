package myDB.supportClasses;

import myDB.workers.Student;

import java.util.ArrayList;

public class Group {
    private ArrayList<Student> list = new ArrayList<>();
    private int groupId;

    public Group(int groupId){
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public ArrayList<Student> getList() {
        return list;
    }

    public void removeStudent(Student st){
        for(int i = 0; i < list.size(); i++){
            Student s = list.get(i);
            if(s.getUn().equals(st.getUn())){
                list.remove(i);
                return;
            }
        }
    }

    public void addStudent(Student st){
        list.add(st);
    }

    public boolean contains(Student st){
        for(Student student : list){
            if(student.getUn().equals(st.getUn())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Group cG = (Group) obj;
        return cG.groupId == this.groupId;
    }

    public int getPopulation(){
        return list.size();
    }
}
