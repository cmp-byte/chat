package models;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class Message2 implements Utils,Comparable<Message2> {
    protected Integer idMessage;
    protected Integer idUser;
    protected Integer idGroup;
    protected String contentText;
    protected LocalDateTime sendTime;

    public Message2(int idMessage, int idUser, int idGroup, LocalDateTime sendTime,String contentText) {
        this.idMessage = idMessage;
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.contentText = contentText;
        this.sendTime = sendTime;
    }

    public static Message2 sendMessage(int idUser,int idGroup,String contentText){
        synchronized (Utils.connectionString) {
            Connection con;
            try {
                con = DriverManager.getConnection(Utils.connectionString, Utils.user, Utils.password);
                String sql = "INSERT INTO chat.messages(id_user,id_group,content_text) VALUES(?,?,?)";
                PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, idUser);
                pstmt.setInt(2, idGroup);
                pstmt.setString(3, contentText.trim());
                if (pstmt.executeUpdate() > 0) {
                    ResultSet resultSet = pstmt.getGeneratedKeys();
                    if (resultSet.next()) {
                        return new Message2(resultSet.getInt(1), idUser, idGroup, LocalDateTime.now(), contentText.trim());
                    }
                }
                return null;
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return null;
            }
        }
    }

    public static Message2 sendMessage(int idUser,int idGroup,String contentText,File file){
        return Message2Complex.sendMessage(idUser,idGroup,contentText,file);
    }

    public static boolean deleteMessage(int idMessage){
        synchronized (Utils.connectionString) {
            Connection con;
            try {
                con = DriverManager.getConnection(connectionString, user, password);
                String sql = "DELETE FROM chat.messages WHERE id_message = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, idMessage);
                if (pstmt.executeUpdate() == 1) {
                    con.close();
                    return true;
                }
                con.close();
                return false;
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return false;
            }
        }
    }

    public static boolean editMessage(int idMessage,String newContent) {
        synchronized (Utils.connectionString) {
            Connection con;
            try {
                con = DriverManager.getConnection(connectionString, user, password);
                String sql = "UPDATE chat.messages SET content_text=? WHERE id_message = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, newContent);
                pstmt.setInt(2, idMessage);
                if (pstmt.executeUpdate() == 1) {
                    con.close();
                    return true;
                }
                con.close();
                return false;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                System.out.println("Conexiunea la baza de date a esuat.");
                return false;
            }
        }
    }

    public boolean sendMessage(){
        if(idUser!=null && idGroup!=null && contentText!=null) {
            Message2 aux = Message2.sendMessage(idUser, idGroup, contentText);
            if (aux != null) {
                this.idMessage=aux.idMessage;
                this.contentText=aux.contentText;
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean editMessage(){
        if(idMessage!=null) {
            return Message2.editMessage(idMessage,contentText);
        }
        return false;
    }

    public boolean deleteMessage(){
        if(idMessage!=null) {
            return Message2.deleteMessage(idMessage);
        }
        return false;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    @Override
    public int compareTo(Message2 o) {
        return this.sendTime.compareTo(o.sendTime);
    }

    public String getContentText() {
        return idUser+": "+contentText;
    }

    public static void main(String[] args) {

        //System.out.println(sendMessage(1,2,"asd",new File("input.txt")));
    }


}
