package myDB.workers;

import myDB.supportClasses.Group;

public class Student extends Worker {

    int group;

    public Student(String un, String name, String surname) {
        super(un, name, surname);
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return super.name;
    }

    @Override
    public String getSurname() {
        return super.surname;
    }

    @Override
    public void setName(String name) {
        super.name = name;
    }

    @Override
    public void setSurname(String surname) {
        super.surname = surname;
    }

    @Override
    public void setWorker(String name, String surname) {
        super.name = name;
        super.surname = surname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Student cG = (Student) obj;
        return cG.getUn() == this.getUn();
    }
}
