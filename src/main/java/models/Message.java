package models;

import com.sun.source.tree.Tree;
import jdk.jshell.execution.Util;
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
import java.util.TreeSet;

public class Message implements IMessage,Utils,Comparable{
    private int idMessage;
    private int idUser;
    private int idGroup;
    private String contentText;
    private File attachement;
    private LocalDateTime sendTime;

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public Message(int idUser, int idGroup, String contentText) {
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.contentText = contentText;
    }

    public Message(int idUser, int idGroup, String contentText, File attachement) {
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.contentText = contentText;
        this.attachement=attachement;
    }

    @Override
    public boolean send() {
        Connection con = null;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            String sql = "INSERT INTO chat.messages(id_user,id_group,content_text,attachement) VALUES(?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1,idUser);
            pstmt.setInt(2,idGroup);
            pstmt.setString(3,contentText);
            String attachment_name = null;
            if(!(attachement==null)){
                attachment_name=send_file(attachement);
            }
            if(attachment_name==null)
                pstmt.setNull(4, Types.NULL);
            else
                pstmt.setString(4,attachment_name);
            System.out.println(pstmt.executeUpdate());
            try {
                ResultSet rs = pstmt.getGeneratedKeys();
                System.out.println(rs.getMetaData());
                //rs.first();
                //System.out.println(rs.getInt(1));
                //System.out.println(rs.getMetaData());
                //this.sendTime = rs.getTimestamp("send_date").toLocalDateTime();

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:3000/api/files/upload");
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
        Connection con = null;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            String sql = "DELETE FROM chat.messages WHERE id_message = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,idMessage);
            if(!(attachement==null)){
                delete_file(attachement.getName());
            }
            if(pstmt.executeUpdate()==1){
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

    private static boolean delete_file(String name){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete("http://localhost:3000/api/files/"+name);
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
    public boolean edit() {
        Connection con = null;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            String sql = "UPDATE chat.messages SET content_text=? WHERE id_message = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1,contentText);
            pstmt.setInt(2,idMessage);
            if(pstmt.executeUpdate()==1){
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

    private static Image get_image(String name){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:3000/api/files/"+name);
        Image result = null;
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity responseEntity = response.getEntity();
            System.out.println(response.getStatusLine());
            byte[] array = EntityUtils.toByteArray(responseEntity);
            result = ImageIO.read(new ByteArrayInputStream(array));
            EntityUtils.consume(responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result==null);
        return result;
    }

    /**
     * From Java Game Engine
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
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

    @Override
    public int compareTo(Object o) {
        Message message = (Message) o;
        return this.sendTime.compareTo(message.sendTime);
    }

    @Override
    public String toString() {
        return "Message{" +
                "idMessage=" + idMessage +
                ", idUser=" + idUser +
                ", idGroup=" + idGroup +
                ", contentText='" + contentText + '\'' +
                ", attachement=" + attachement +
                ", sendTime=" + sendTime +
                '}';
    }

    public static void main(String[] args) {
        // System.out.println(send_file(new File("input.png")));
        // ImageIO.write(toBufferedImage(get_image("QK9imhWFvcU8tBV.png")),"png",new File("output.png"));

        Message message = new Message(1,1,"asdgh");
        message.send();
        //message.send();
        System.out.println(message);


        /*
        TreeSet<Message> treeSet = new TreeSet<>();
        treeSet.add(message);
        treeSet.add(message);
        treeSet.add(message);
        treeSet.add(message);
        treeSet.add(message);
        System.out.println(treeSet);
         */

        //System.out.println(message.send());
        //message.setIdMessage(2);
        //message.edit();
    }
}
