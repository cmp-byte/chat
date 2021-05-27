package models;

import java.sql.SQLException;
import java.util.List;

public interface IUser {
    public  boolean signup() throws SQLException;
    public boolean login() throws SQLException;
    public boolean logout();
    public List<User> search() throws SQLException;
}
