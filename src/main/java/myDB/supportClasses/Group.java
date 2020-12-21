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

    public void addStudent(Student st){
        list.add(st);
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
}
