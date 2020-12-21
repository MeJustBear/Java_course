package myDB.workers;

import myDB.workers.Worker;

public class Admin extends Worker {

    public Admin(String un, String name, String surname) {
        super(un, name, surname);
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
