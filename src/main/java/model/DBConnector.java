package model;

import myDB.*;
import myDB.workers.Worker;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class DBConnector {
    private static myDataBase dataBase;
    private static DBConnector instance;
    private DBConnector() {
        try {
            dataBase = new myDataBase();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static DBConnector getInstance(){

        if(instance == null){
            instance = new DBConnector();
        }
        return instance;
    }

    public boolean checkUsernamePassword(String username, String password){
        return dataBase.findPas(username,password);
    }

    public HashMap<String, ArrayList<Subject>> getSubjects(Object obj){
        String name = (String) obj;
        return null;
    }

    public String getFullName(Object obj) {
        String name = (String) obj;
        HashMap<String, List<Worker>> workers = dataBase.getWorkers();
        if(name.startsWith("st")){
            List<Worker> workerList = workers.get("Student");
            for(Worker w: workerList){
                if(w.getUn().equals(name)){
                    StringBuilder sb = new StringBuilder();
                    sb.append(w.getName());
                    sb.append(" ");
                    sb.append(w.getSurname());
                    return sb.toString();
                }
            }
        }
        return null;
    }

    public Worker getWorker(Object obj) {
        String name = (String) obj;
        HashMap<String, List<Worker>> workers = dataBase.getWorkers();
        if(name.startsWith("st")){
            List<Worker> workerList = workers.get("Student");
            for(Worker w: workerList){
                if(w.getUn().equals(name)){
                    return w;
                }
            }
        }
        return null;
    }

}
