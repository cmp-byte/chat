package models;

import java.util.List;

public interface IUser {
    public  boolean signup();
    public boolean login();
    public boolean logout();
    public List<User> search();
}
