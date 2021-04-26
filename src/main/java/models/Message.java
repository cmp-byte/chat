package models;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class Message implements IMessage,Utils,Comparable<Message>{
    private int idMessage;
    private int idUser;
    private int idGroup;
    private String contentText;
    private String attachment;
    private LocalDateTime sendTime;

    public Message(int idMessage, int idUser, int idGroup, LocalDateTime sendTime,String contentText, String attachment) {
        this.idMessage = idMessage;
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.contentText = contentText;
        this.attachment = attachment;
        this.sendTime = sendTime;
    }

    public Message(int idUser, int idGroup, String contentText) {
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.contentText = contentText;
    }

    public Message(int idUser, int idGroup, String contentText, String attachment) {
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.contentText = contentText;
        this.attachment=attachment;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean send() {
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            String sql = "INSERT INTO chat.messages(id_user,id_group,content_text,attachment) VALUES(?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,idUser);
            pstmt.setInt(2,idGroup);
            pstmt.setString(3,contentText);
            String attachment_name = null;
            if(!(attachment==null)){
                attachment_name=send_file(new File("./temp/"+attachment));
                new File("./temp/"+attachment).renameTo(new File("./temp/"+attachment_name));
            }
            if(attachment_name==null)
                pstmt.setNull(4, Types.NULL);
            else
                pstmt.setString(4,attachment_name);
            if(!(pstmt.executeUpdate()==1)){
                con.close();
                return false;
            }
            try {
                // TODO: GET HELP WITH AUTOGENERATED KEYS
                Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmp.executeQuery("SELECT * from chat.messages where id_message=LAST_INSERT_ID()");
                rs.first();
                idMessage=rs.getInt("id_message");
                idUser=rs.getInt("id_user");
                idGroup=rs.getInt("id_group");
                sendTime = rs.getTimestamp("send_date").toLocalDateTime();
                contentText=rs.getString("content_text");
                attachment=rs.getString("attachment");
                con.close();
                return true;
            }
            catch(NullPointerException nullPointerException){
                con.close();
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Conexiunea la baza de date a esuat.");
            return false;
        }
    }
    private static String send_file(File file){
        if(!(file.exists())){
            return null;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(fileServerURL+"api/files/upload");
        HttpEntity entity = MultipartEntityBuilder.create().addPart("photo", new FileBody(file)).build();
        post.setEntity(entity);
        String result = null;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            HttpEntity responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity);
            EntityUtils.consume(responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public boolean delete() {
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            String sql = "DELETE FROM chat.messages WHERE id_message = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,idMessage);
            if(!(attachment==null)){
                delete_file(attachment);
            }
            if(pstmt.executeUpdate()==1){
                con.close();
                idMessage=-1;
                idUser=-1;
                idGroup=-1;
                contentText=null;
                attachment=null;
                sendTime=null;
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
    private static boolean delete_file(String name){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(fileServerURL+"api/files/"+name);
        try (CloseableHttpResponse response = httpclient.execute(httpDelete)) {
            StatusLine status = response.getStatusLine();
            if(status.getStatusCode()==200){
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    @Override
    public boolean edit(String newContent) {
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            String sql = "UPDATE chat.messages SET content_text=? WHERE id_message = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1,newContent);
            pstmt.setInt(2,idMessage);
            if(pstmt.executeUpdate()==1){
                con.close();
                contentText=newContent;
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
    private static Image get_image(String name){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(fileServerURL+"api/files/"+name);
        Image result = null;
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity responseEntity = response.getEntity();
            byte[] array = EntityUtils.toByteArray(responseEntity);
            result = ImageIO.read(new ByteArrayInputStream(array));
            EntityUtils.consume(responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * From Java Game Engine
     * Converts a given Image into a BufferedImage
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }
    //For testing:
    //System.out.println(send_file(new File("input.png")));
    //ImageIO.write(toBufferedImage(get_image("QK9imhWFvcU8tBV.png")),"png",new File("output.png"));
    @Override
    public int compareTo(Message o) {
        return this.sendTime.compareTo(o.sendTime);
    }
    @Override
    public String toString() {
        return "Message{" +
                "idMessage=" + idMessage +
                ", idUser=" + idUser +
                ", idGroup=" + idGroup +
                ", contentText='" + contentText + '\'' +
                ", attachment=" + attachment +
                ", sendTime=" + sendTime +
                '}';
    }
    public static void main(String[] args)  {
        // In temp sunt salvate toate fisierele care vor fi in baza de date, de exemplu cand adaugam un fisier, momentan prima oara il adaugam in folder-ul
        // temp si dupa aceia se prelucreaza... vedem daca schimbam.
        // Fisierul ala cu de alea la intamplare, asa arata in baza de date, asa o sa ajunga si heyo
        // Putem sa trimitem orice chestie dar dupa sa vedem cum procesam... de asta am schimbat in attachment
        // Avem user-ul cu ID 1 si grupul cu ID 1
        // Cream un mesaj doar cu text;
        Message message = new Message(1,1,"heyo!!");
        System.out.println(message);
        message.send();// trimitem mesajul, daca se adauga cu succes atunci nu plange interpretorul si da true iar
                        // continutul mesajului se schimba, primeste id_message si send date
        System.out.println(message);
        message.edit("heyo dar de data asta editat"); // editam mesajul iar daca este cu succes se schimba
        System.out.println(message);
        message.delete(); // stergem din baza de date si stergem continutul mesajului
        System.out.println(message);

        message = new Message(1,1,"heyo!!","heyo.jpg");
        System.out.println(message);
        message.send();// trimitem mesajul, daca se adauga cu succes si se schimba numele fisierului ( se garanteaza unicitatea )
        System.out.println(message);
    }
}
