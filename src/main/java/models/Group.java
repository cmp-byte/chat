package models;

import java.util.List;

public class Group implements IGroup{
    private int idGroup;
    private String title;
    private List <User> users;
    private List <Message> messages;

    @Override
    public boolean rename() {
        return false;
    }

    @Override
    public boolean add() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
