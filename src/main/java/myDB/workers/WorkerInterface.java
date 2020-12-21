package myDB.workers;

public interface WorkerInterface {
    public String getName();
    public String getSurname();
    public void setName(String name);
    public void setSurname(String surname);
    public void setWorker(String name,String surname);
    public String getUn();
}
