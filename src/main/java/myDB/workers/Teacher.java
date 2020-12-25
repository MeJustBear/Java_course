package myDB.workers;

import java.util.ArrayList;

public class Teacher extends Worker{

    ArrayList<String> subjects = new ArrayList<>();

    public Teacher(String un, String name, String surname) {
        super(un, name, surname);
    }

    public void setSubjects(ArrayList<String> subjs){
        subjects = subjs;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void addSubject(String sub){
        subjects.add(sub);
    }

    public void removeSubject(String subj){
        subjects.remove(subjects.indexOf(subj));
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
}
