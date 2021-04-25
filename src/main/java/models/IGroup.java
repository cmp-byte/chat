package models;

import java.io.IOException;

public interface IGroup {
    public boolean rename()throws IOException, java.sql.SQLException;
    public boolean add() throws java.sql.SQLException;
    public boolean delete()throws java.sql.SQLException;
}
