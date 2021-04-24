package models;

import java.awt.image.BufferedImage;

public class Message implements IMessage{
    private int idMessage;
    private int idUser;
    private int idGroup;
    private String type;
    private String contentText;
    private BufferedImage contentImage;

    @Override
    public boolean send() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public boolean edit() {
        return false;
    }
}
