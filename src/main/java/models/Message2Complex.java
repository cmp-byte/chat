package models;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class Message2Complex extends Message2 {
    private String attachment;

    public Message2Complex(int idMessage, int idUser, int idGroup, LocalDateTime sendTime, String contentText, String attachment) {
        super(idMessage,idUser,idGroup,sendTime,contentText);
        this.attachment=attachment;
    }

    public static Message2 sendMessage(int idUser,int idGroup,String contentText, File file){
        synchronized (Utils.connectionString) {
            if (file == null)
                return Message2.sendMessage(idUser, idGroup, contentText);
            Connection con;
            try {
                con = DriverManager.getConnection(Utils.connectionString, Utils.user, Utils.password);
                String sql = "INSERT INTO chat.messages(id_user,id_group,content_text,attachment) VALUES(?,?,?,?)";
                PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, idUser);
                pstmt.setInt(2, idGroup);
                pstmt.setString(3, contentText);
                String fileName = sendFile(file);
                if (fileName == null) {
                    return null;
                }
                pstmt.setString(4, fileName);
                if (pstmt.executeUpdate() > 0) {
                    ResultSet resultSet = pstmt.getGeneratedKeys();
                    if (resultSet.next()) {
                        return new Message2Complex(resultSet.getInt(1), idUser, idGroup, LocalDateTime.now(), contentText, fileName);
                    }
                }
                return null;
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return null;
            }
        }
    }

    private static String sendFile(File file){
        synchronized (Utils.connectionString) {
            if (!(file.exists())) {
                return null;
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost post = new HttpPost(fileServerURL + "api/files/upload");
            HttpEntity entity = MultipartEntityBuilder.create().addPart("photo", new FileBody(file)).build();
            post.setEntity(entity);
            String result = null;
            try (CloseableHttpResponse response = httpclient.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                result = EntityUtils.toString(responseEntity);
                EntityUtils.consume(responseEntity);
            } catch (IOException e) {
                //e.printStackTrace();
            }
            return result;
        }
    }

    public static boolean deleteMessage(int idMessage, String fileName){
        synchronized (Utils.connectionString) {
            Connection con;
            try {
                con = DriverManager.getConnection(connectionString, user, password);
                String sql = "DELETE FROM chat.messages WHERE id_message = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, idMessage);
                delete_file(fileName);
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
    public boolean sendMessage(){
        if(idUser!=null && idGroup!=null && contentText!=null && attachment!=null) {
            Message2Complex aux = (Message2Complex) Message2Complex.sendMessage(idUser, idGroup, contentText, new File(attachment));
            if (aux != null) {
                this.idMessage=aux.idMessage;
                this.contentText=aux.contentText;
                this.attachment= aux.attachment;
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String getContentText() {
        return idUser+": "+contentText+"Acceseaza attachementul la "+Utils.fileServerURL+"api/files/"+attachment;
    }

}
