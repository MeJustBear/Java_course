package myDB.workers;

abstract public class Worker implements WorkerInterface {
    protected String un;
    protected String name;
    protected String surname;
    public Worker(String un, String name, String surname){
            this.un = un;
            this.name = name;
            this.surname = surname;
    }

    @Override
    public String getUn() {
        return un;
    }

}
