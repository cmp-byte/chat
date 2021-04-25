package models;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Group implements IGroup,Utils{
    private int idGroup;
    private String title;
    private List <User> users;
    private TreeSet<Message> messages;
    private Integer page = 0;

    public Group(int idGroup) {
        this.idGroup = idGroup;
        this.title = null;
        this.users = new ArrayList<User>();
        this.messages = new TreeSet<Message>();
    }

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


    public boolean getMessages(int page){
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmp.executeQuery("SELECT * from chat.messages where id_group="+idGroup+" ORDER BY send_date asc LIMIT 25 OFFSET "+25*page);
            while(rs.next()){
                Message message = new Message(rs.getInt("id_message"),
                        rs.getInt("id_user"),
                        rs.getInt("id_group"),
                        rs.getTimestamp("send_date").toLocalDateTime(),
                        rs.getString("content_text"),
                        rs.getString("attachment")
                );
                if(message.getAttachment()!=null){
                    get_file(message.getAttachment());
                }
                messages.add(message);
            }
            con.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean getMessages(){
        if(this.getMessages(page)){
            page++;
            return true;
        }
        return false;

    }

    private static void get_file(String name){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(fileServerURL+"api/files/"+name);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity responseEntity = response.getEntity();
            byte[] array = EntityUtils.toByteArray(responseEntity);
            try(FileOutputStream stream = new FileOutputStream("./temp/"+name)){
                stream.write(array);
            }
            EntityUtils.consume(responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Group{" +
                "idGroup=" + idGroup +
                ", title='" + title + '\'' +
                ", users=" + users +
                ", messages=" + messages +
                '}';
    }

    public static void main(String[] args) {
        Group group= new Group(1);
        group.getMessages();
        System.out.println(group);
    }
}
