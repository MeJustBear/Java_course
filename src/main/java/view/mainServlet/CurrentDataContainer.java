package view.mainServlet;

import myDB.supportClasses.lesson;

public class CurrentDataContainer {

    protected static class CurrentLesson{
        String curSubject = null;
        String teacherId = null;
        int groupId = 0;
        lesson curLesson = null;
    }

    private class CurrentSubjects{

    }

    private static CurrentLesson currentLesson;
    private static int groupId;


    private static CurrentDataContainer instance = null;

    private CurrentDataContainer(){

    }

    public static CurrentDataContainer getInstance(int groupId) {
        if(instance == null){
            instance = new CurrentDataContainer();
            currentLesson = new CurrentLesson();
            groupId = groupId;
        }
        return instance;
    }

    public void setCurrentLesson(String subjName, String teacherId, lesson les,int groupId){
        currentLesson.curSubject = subjName;
        currentLesson.teacherId = teacherId;
        currentLesson.groupId = groupId;
        currentLesson.curLesson = les;
    }

}
