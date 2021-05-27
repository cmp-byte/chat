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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Group implements IGroup,Utils {
    private int idGroup = 0;
    private String title;
    private ArrayList<User> users = new ArrayList<>();
    private TreeSet<Message> messages = new TreeSet<>();
    private TreeSet<Message2> messages2 = new TreeSet<>();
    //private Integer page = 0;
    //static int limit = 5; // limit per page
    Long last_search;

    public Group() {
        // constructor fara paramentrii , util pt testarea functiilor din clasa
    }

    public Group(int idGroup, String title) {
        this.idGroup = idGroup;
        this.title = title;
        messages = new TreeSet<>();
    }

    public void deleteUserFromGroup(int id){
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            stmp.executeUpdate("delete from user_group where id_user="+id+" and id_group="+idGroup);
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void deleteGroup(){
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            stmp.executeUpdate("delete from groups where id_group="+idGroup);
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int getCurrentId() throws java.sql.SQLException { // in functia asta vreau sa iau id-ul maxim atribuit unui grup din baza de date
        // va fi utila pentru constructor , deoarece eu daca inchid si redeschid aplicatia nu mai stiu unde am ajuns
        //dar pot afla mereu cu ajutorul datelor care raman in baza de date! Ura!
        int id_group = 0;
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "Cmp", "1237"); //deschid conexiunea
        Statement statement = connection.createStatement(); // fac un obiect de tip statement
        String query = "select max(id_group) as id_group from chat.groups "; // voi lua ultimul id la care am ramas
        ResultSet rs = statement.executeQuery(query); // este foarte imp in query sa ii dam un alias altfel coloana se va numi implicit max(id_group)
        if (rs.next())
            id_group = rs.getInt("id_group");
        return id_group;
    }

    ///CONSTRUCTOR
    public Group(ArrayList<User> users) throws java.sql.SQLException {
        this.idGroup = getCurrentId() + 1; //ramane de testat daca a mers
        this.users = users;
        // nu cred ca am de ce sa am o lista de mesaje in constructor din moment ce implicit va fi goala
        try {
            if (this.users.size() >= 2);
                //this.title = this.users.get(0).getFirstName() + "_" + this.users.get(1).getFirstName();// conform cerintei titlul implicit va fi format din numele celor 2 participanti
                //this.users.get(0).getFirstName e echivalentul in c++ pentru un vector de genul this->Vector[0].getWhatever();

            else throw new Exception(); //daca vectorul nu are o dimensiune adecvata  aruncam o exceptie

        } catch (Exception cmpStyleException) {
            System.out.println("Numarul de participanti pentru a crea un grup trebuie sa fie minim 2");
        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "Cmp", "1237"); //deschid conexiunea
        String query = "insert into chat.groups(title) values(?)"; // scriu query
        PreparedStatement preparedStatement = connection.prepareStatement(query);// fac un prepared statment pt ca am nev de parametru
        preparedStatement.setString(1, this.title); // primul ? va avea valoarea titlului curent
        preparedStatement.executeUpdate();// vreau sa actualizez in baza de date noua val inserata . id-ul va fi generat automat
        connection.close();
    }

    @Override
    public boolean rename() throws IOException, java.sql.SQLException {
        Scanner scanner = new Scanner(System.in); //scaner care citeste de la tastatura
        System.out.println("Enter new title: ");
        String s = scanner.nextLine(); //citesc noul titlu
        this.title = s; // actualizez in obiect
        //voi actualiza in baza de date
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "Cmp", "1237"); //deschid conexiunea
        String query = "update chat.groups set title = ? where id_group = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, this.title);
        preparedStatement.setInt(2, this.idGroup);
        //folosesc un prepared statement , am nev de paramentrii ; in final imi va actualiza coloana title din tabel cu noua val unde PK coincide cu idGroup
        preparedStatement.executeUpdate();
        connection.close();
        return true;
    }

    public boolean my_rename(String title){
        Connection connection = null; //deschid conexiunea
        try {
            connection = DriverManager.getConnection(Utils.connectionString, Utils.user, Utils.password);
            String query = "update groups set title = ? where id_group = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, this.idGroup);
            if(preparedStatement.executeUpdate()>0){
                connection.close();
                this.title = title;
                return true;
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean add(User user) throws java.sql.SQLException {
        //Ce inseamna sa adaug inca o persoana conversatiei/grupului?
        // Inseamna sa ii crez o legatura user_group in baza de date, deci asta vom face cu aceasta functie.
        Connection connection = DriverManager.getConnection(connectionString, Utils.user, Utils.password); //deschid conexiunea
        String query = "insert into user_group (id_user,id_group) values(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user.getIdUser());
        preparedStatement.setInt(2, this.idGroup);
        preparedStatement.executeUpdate();
        connection.close();
        //Adaugam in vectorul de participanti unul nou
        users.add(user); // doar daca nu da eroare
        System.out.println("User adaugat cu succes!");
        return true;
    }

    public static Group create(User user1,User user2) {
        Group group = null;
        Connection connection = null; //deschid conexiunea
        try {
            connection = DriverManager.getConnection(Utils.connectionString, Utils.user, Utils.password);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO groups(title) values( (select CONCAT(last_name,' ',first_name,\" AND \", (select CONCAT(last_name,' ',first_name) from Users where id_user = ?) ) from Users where id_user = ?) )",Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, user2.getIdUser());
            preparedStatement.setInt(2, user1.getIdUser());
            if(preparedStatement.executeUpdate()>0){
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if(rs.next()){
                    int generated_id = rs.getInt(1);
                    System.out.println(generated_id);
                    preparedStatement = connection.prepareStatement("INSERT INTO user_group(id_user,id_group) values(?,?),(?,?)");
                    preparedStatement.setInt(1,user1.getIdUser());
                    preparedStatement.setInt(2,generated_id);
                    preparedStatement.setInt(3,user2.getIdUser());
                    preparedStatement.setInt(4,generated_id);
                    if(preparedStatement.executeUpdate()>0) {
                        group = new Group();
                        group.idGroup = generated_id;
                        group.title = user1.getLastName() + " " + user1.getFirstName()  + " AND " + user2.getLastName() + " " + user2.getFirstName();
                        group.users = new ArrayList<>();
                        group.messages = new TreeSet<>();
                        group.users.add(user1);
                        group.users.add(user2);
                    }
                }
            }
            connection.close();
        } catch (SQLException ignored) { ignored.printStackTrace();}
        return group;
    }

    @Override
    public boolean delete() throws java.sql.SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "Cmp", "1237"); //deschid conexiunea
        String query = "delete from groups where id_group=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, this.idGroup);
        connection.close();
        //Sterg grupul dupa group_id
        return true;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }


    /*
    public boolean getNewMessages(int page){
        if(page==0){
            last_search=System.currentTimeMillis();
        }
        Connection con;
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmp.executeQuery("SELECT * from chat.messages where id_group="+idGroup+" ORDER BY send_date desc LIMIT "+limit+" OFFSET "+limit*page);
            while(rs.next()){
                Message message = new Message(rs.getInt("id_message"),
                        rs.getInt("id_user"),
                        rs.getInt("id_group"),
                        rs.getTimestamp("send_date").toLocalDateTime(),
                        rs.getString("content_text"),
                        rs.getString("attachment")
                );
                if(message.getAttachment()!=null){
                    if(!get_file(message.getAttachment())){
                        message.setAttachment(null);
                    }
                }
                if(message!=null)
                    messages.add(message);
            }
            con.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean getNewMessages(){
        if(this.getNewMessages(page)){
            page++;
            return true;
        }
        return false;
    }
    */

    public ArrayList<Message> getOlderMessages(){
        long search;
        if(messages==null || messages.size()==0){
            search = System.currentTimeMillis();
            last_search = search;
        } else {
            Instant instant = messages.first().getSendTime().atZone(ZoneId.systemDefault()).toInstant();
            search = instant.toEpochMilli();
        }
        Connection con;
        ArrayList<Message> messagesReturned = new ArrayList<>();
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmp.executeQuery("SELECT * from messages where id_group="+idGroup+" and send_date<='"+print_date_time_in_ms(search,"YYYY-MM-d HH:mm:ss.SSSS") +"' ORDER BY send_date desc LIMIT 5" );
            while(rs.next()){
                Message message = new Message(rs.getInt("id_message"),
                        rs.getInt("id_user"),
                        rs.getInt("id_group"),
                        rs.getTimestamp("send_date").toLocalDateTime(),
                        rs.getString("content_text"),
                        rs.getString("attachment")
                );
                if(message.getAttachment()!=null){
                    if(!get_file(message.getAttachment())){
                        message.setAttachment(null);
                    }
                }
                messages.add(message);
                messagesReturned.add(message);
            }
            con.close();
            last_search=System.currentTimeMillis();
            return messagesReturned;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<Message2> getOlderMessages2(){
        long search;
        if(messages2==null || messages2.size()==0){
            search = System.currentTimeMillis();
            last_search = search;
        } else {
            Instant instant = messages2.first().getSendTime().atZone(ZoneId.systemDefault()).toInstant();
            search = instant.toEpochMilli();
        }
        Connection con;
        ArrayList<Message2> messagesReturned = new ArrayList<>();
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmp.executeQuery("SELECT * from messages where id_group="+idGroup+" and send_date<='"+print_date_time_in_ms(search,"YYYY-MM-d HH:mm:ss.SSSS") +"' ORDER BY send_date desc LIMIT 5" );
            while(rs.next()){
                int id_message = rs.getInt("id_message");
                int id_user = rs.getInt("id_user");
                int id_group = rs.getInt("id_group");
                LocalDateTime localDateTime = rs.getTimestamp("send_date").toLocalDateTime();
                String content_text = rs.getString("content_text");
                String attachment = rs.getString("attachment");
                Message2 message = null;
                if(attachment!=null){
                    message = new Message2Complex(id_message,id_user,id_group,localDateTime,content_text,attachment);
                } else{
                    message = new Message2(id_message,id_user,id_group,localDateTime,content_text);
                }
                messagesReturned.add(message);
                messages2.add(message);
            }
            con.close();
            last_search=System.currentTimeMillis();
            messagesReturned.sort(Message2::compareTo);
            return messagesReturned;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<Message> getNewestMessages(){
        synchronized (Utils.connectionString) {
            if (last_search == null) {
                return getOlderMessages();
            }
            Connection con;
            ArrayList<Message> messagesReturned = new ArrayList<>();
            try {
                con = DriverManager.getConnection(connectionString, user, password);
                Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmp.executeQuery("SELECT * from messages where id_group=" + idGroup + " and send_date>='" + print_date_time_in_ms(last_search, "YYYY-MM-d HH:mm:ss.SSSS") + "' ORDER BY send_date desc");
                while (rs.next()) {
                    Message message = new Message(rs.getInt("id_message"),
                            rs.getInt("id_user"),
                            rs.getInt("id_group"),
                            rs.getTimestamp("send_date").toLocalDateTime(),
                            rs.getString("content_text"),
                            rs.getString("attachment")
                    );
                    if (message.getAttachment() != null) {
                        if (!get_file(message.getAttachment())) {
                            message.setAttachment(null);
                        }
                    }
                    messagesReturned.add(message);
                    messages.add(message);
                }
                con.close();
                last_search = System.currentTimeMillis();
                return messagesReturned;
            } catch (SQLException throwables) {
                throwables.printStackTrace();

            }
            return null;
        }
    }

    public ArrayList<Message2> getNewestMessages2(){
        synchronized (Utils.connectionString) {
            if (last_search == null) {
                return getOlderMessages2();
            }
            Connection con;
            ArrayList<Message2> messagesReturned = new ArrayList<>();
            try {
                con = DriverManager.getConnection(connectionString, user, password);
                Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmp.executeQuery("SELECT * from messages where id_group=" + idGroup + " and send_date>='" + print_date_time_in_ms(last_search, "YYYY-MM-d HH:mm:ss.SSSS") + "' ORDER BY send_date desc");
                while (rs.next()) {
                    int id_message = rs.getInt("id_message");
                    int id_user = rs.getInt("id_user");
                    int id_group = rs.getInt("id_group");
                    LocalDateTime localDateTime = rs.getTimestamp("send_date").toLocalDateTime();
                    String content_text = rs.getString("content_text");
                    String attachment = rs.getString("attachment");
                    Message2 message = null;
                    if(attachment!=null){
                        message = new Message2Complex(id_message,id_user,id_group,localDateTime,content_text,attachment);
                    } else{
                        message = new Message2(id_message,id_user,id_group,localDateTime,content_text);
                    }
                    messagesReturned.add(message);
                    messages2.add(message);

                }
                con.close();
                last_search = System.currentTimeMillis();
                messagesReturned.sort(Message2::compareTo);
                return messagesReturned;
            } catch (SQLException throwables) {
                throwables.printStackTrace();

            }
            return null;
        }
    }

    private static boolean get_file(String name){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(fileServerURL+"api/files/"+name);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity responseEntity = response.getEntity();
            byte[] array = EntityUtils.toByteArray(responseEntity);
            try(FileOutputStream stream = new FileOutputStream("./temp/"+name)){
                stream.write(array);
            }
            EntityUtils.consume(responseEntity);
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }


    public static ArrayList<Group> getGroups(int idUser){
        synchronized (Utils.connectionString) {
            System.out.println("1");
            ArrayList<Group> groups = new ArrayList<>();
            Connection con;
            try {
                con = DriverManager.getConnection(connectionString, user, password);
                Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmp.executeQuery("select groups.id_group,groups.title from groups join user_group on groups.id_group=user_group.id_group where id_user = " + idUser);
                while (rs.next()) {
                    Group group = new Group(rs.getInt("id_group"), rs.getString("title"));
                    groups.add(group);
                }
                con.close();
                return groups;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        }
    }



    public TreeSet<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return title + " " + idGroup;
    }


    private static LocalDateTime get_date_time_in_ms(String zone,long data){
        Instant instant = Instant.ofEpochMilli(data);
        ZonedDateTime utcZoned = instant.atZone(ZoneId.of("UTC"));
        ZonedDateTime ldtZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime ldt = ldtZoned.toLocalDateTime();
        return ldt;
    }
    private static LocalDateTime get_date_time_in_ms(long data){
        Instant instant = Instant.ofEpochMilli(data);
        ZonedDateTime utcZoned = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime ldtZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime ldt = ldtZoned.toLocalDateTime();
        return ldt;
    }

    private static String print_date_time_in_ms(String zone,long data,String pattern){
        // Patern form: dd-MM-yyyy
        LocalDateTime ldt = get_date_time_in_ms(zone,data);
        String str = ldt.format(DateTimeFormatter.ofPattern(pattern));
        return str;
    }
    private static String print_date_time_in_ms(long data,String pattern){
        // Patern form: dd-MM-yyyy
        LocalDateTime ldt = get_date_time_in_ms(data);
        String str = ldt.format(DateTimeFormatter.ofPattern(pattern));
        return str;
    }
    public void get_users(){
        Connection con;
        users = new ArrayList<>();
        try {
            con= DriverManager.getConnection(connectionString,user,password);
            Statement stmp = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmp.executeQuery("select users.id_user,users.last_name,users.first_name from users\n" +
                    "join user_group using(id_user)\n" +
                    "where user_group.id_group= "+idGroup);
            while(rs.next()){
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                users.add(user);
            }
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }


    public ArrayList<User> getUsers() {
        return users;
    }

    public static void main(String[] args) {
        Group group = new Group();
        group.setIdGroup(12);
        group.getOlderMessages();
        group.getOlderMessages();
        group.getOlderMessages();
        group.getOlderMessages();
        System.out.println(group.getMessages().stream().count());
    }

    public String getContentText(Message entered) {
        String [] cuvinte = entered.getContentText().split(":",2);
        if(users==null || users.stream().count()==0 || users.stream().map(User::getIdUser).collect(Collectors.toList()).contains(Integer.parseInt(cuvinte[0]))){
            this.get_users();
        }
        String nume= users.stream().filter(i->i.getIdUser()==Integer.parseInt(cuvinte[0])).map(User::getCompleteName).collect(Collectors.joining(","));
        return nume+": "+cuvinte[1];
    }


    public String getContentText(Message2 entered) {
        String [] cuvinte = entered.getContentText().split(":",2);
        if(users==null || users.stream().count()==0 || users.stream().map(User::getIdUser).collect(Collectors.toList()).contains(Integer.parseInt(cuvinte[0]))){
            this.get_users();
        }
        String nume= users.stream().filter(i->i.getIdUser()==Integer.parseInt(cuvinte[0])).map(User::getCompleteName).collect(Collectors.joining(","));
        return nume+": "+cuvinte[1];
    }

    public TreeSet<Message2> getMessages2() {
        return messages2;
    }
}
