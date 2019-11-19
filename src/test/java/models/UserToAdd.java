package models;

public class UserToAdd {
    private String name;
    private String job;

    public UserToAdd(String name, String job) {
        this.name = name;
        this.job = job;
    }
    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }

}
