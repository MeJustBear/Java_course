package model;

import myDB.*;

import java.io.FileNotFoundException;

public  class DBConnector {
    private static myDataBase dataBase;
    private static DBConnector instance;
    private DBConnector() {
        try {
            dataBase = new myDataBase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DBConnector getInstance(){

        if(instance == null){
            instance = new DBConnector();
        }
        return instance;
    }
}
