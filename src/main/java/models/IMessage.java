package models;

public interface IMessage {
    public boolean send();
    public boolean delete();
    public boolean edit(String newContent);
}
