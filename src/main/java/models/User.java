package models;

import jdk.jshell.execution.Util;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User implements IUser, Utils{
    private  int idUser;
    private String lastName;
    private String firstName;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private String password;
    private boolean loginStatus;

    public User(){}
    public User(int idUser, String lastName, String firstName, String email, String gender, LocalDate birthDate, String password) {
        this.idUser = idUser;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.password = password;
    }

    public User(int idUser, String lastName, String firstName, String email, String gender, String password) {
        this.idUser = idUser;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.gender = gender;
        this.password = password;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean signup() throws SQLException {
        boolean insCheck=false;
        Connection conn = null;
        conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat","bxoing", "1237");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter LastName");
        String lastName = myObj.nextLine();  // Read user input
        System.out.println("Enter FirstName");
        String firstName = myObj.nextLine();  // Read user input
        System.out.println("Enter Email");
        String email = myObj.nextLine();  // Read user input
        System.out.println("Enter Gender");
        String gender = myObj.nextLine();  // Read user input
        System.out.println("Enter BirthDate");
        LocalDate bithrDate = LocalDate.parse(myObj.nextLine(), DateTimeFormatter.ofPattern("d/MM/yyyy"));  // Read user input
        System.out.println("Enter Password");
        String password= myObj.nextLine();  // Read user input
        Statement stmt = conn.createStatement();
        String query="insert into chat.users(last_name,first_name,email,gender,birth_date,password)" +
                " values(?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1,lastName);
        preparedStmt.setString(2,firstName);
        preparedStmt.setString(3,email);
        preparedStmt.setString(4,gender);
        preparedStmt.setDate(5, Date.valueOf(bithrDate));
        preparedStmt.setString(6,password);
        int count = preparedStmt.executeUpdate();
        if (count > 0)
            insCheck=true;
        conn.close();
        return insCheck;
    }


    public static boolean my_signup(String lastName,String firstName, String email, String gender, LocalDate birthDate, String password) throws SQLException {
        boolean insCheck=false;
        Connection conn = null;
        conn = DriverManager.getConnection(Utils.connectionString,Utils.user, Utils.password);
        Statement stmt = conn.createStatement();
        String query="insert into chat.users(last_name,first_name,email,gender,birth_date,password)" +
                " values(?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1,lastName);
        preparedStmt.setString(2,firstName);
        preparedStmt.setString(3,email);
        preparedStmt.setString(4,gender);
        preparedStmt.setDate(5, Date.valueOf(birthDate));
        preparedStmt.setString(6,password);
        int count = preparedStmt.executeUpdate();
        if (count > 0)
            insCheck=true;
        conn.close();
        return insCheck;
    }

    @Override
    public boolean login() throws SQLException {
        boolean loginCheck=false;
        Connection conn = null;
        conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat","bxoing", "1237");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Email");
        String email = myObj.nextLine();  // Read user input
        System.out.println("Enter Password");
        String password= myObj.nextLine();  // Read user input
        Statement stmt = conn.createStatement();
        String query="SELECT email,password FROM chat.users WHERE email="+"'"+email+"'"+"AND password="+password;
        ResultSet rs=stmt.executeQuery(query);
        if(rs.next()) {
            loginCheck=true;
        }
        this.loginStatus=true;
        return loginCheck;
    }

    public static User my_login(String email, String password) throws SQLException {
        User user = null;
        if(email==null || password==null){
            return user;
        }
        Connection conn = null;
        conn = DriverManager.getConnection(Utils.connectionString,Utils.user, Utils.password);
        Statement stmt = conn.createStatement();
        String query="SELECT id_user,first_name,last_name,email,gender,birth_date FROM   users WHERE email="+"'"+email+"'"+"AND password="+"'"+password+"'";
        ResultSet rs=stmt.executeQuery(query);
        if(rs.next()) {
            user = new User();
            user.setIdUser(rs.getInt("id_user"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setGender(rs.getString("gender"));
            user.setBirthDate(rs.getDate("birth_date").toLocalDate());
        }
        return user;
    }

    @Override
    public boolean logout() {
        this.loginStatus=false;
        return loginStatus;
    }

    public List<User> search() throws SQLException {
        if (!loginStatus)
            return null;
        else {
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat", "bxoing", "1237");
            Statement stmt = conn.createStatement();
            List<User> listaUser = new ArrayList<>();
            System.out.printf("Dupa ce criteriu doriti sa cautati ? \n" +
                    "Pentru nume apasati 1 \n" + "Pentru prenume apasati 2 \n" +
                    "Pentru email apasati 3 \n"
            );
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Introduceti cifra dorita");
            String criteriu = myObj.nextLine();
            switch (criteriu) {
                case "1":
                    System.out.println("Introduceti numele");
                    String lastName = myObj.nextLine();  // Read user input
                    String query = "SELECT * FROM   users WHERE last_name=" + "'" + lastName + "'";
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String lastname = rs.getString(2);
                        String firstname = rs.getString(3);
                        String email = rs.getString(4);
                        String gender = rs.getString(5);
                        Date input = rs.getDate(6);
                        System.out.println(input);
                        LocalDate birthdate = input.toLocalDate();
                        String password = rs.getString(7);
                        User ob = new User(id, lastname, firstname, email, gender, birthdate, password);
                        listaUser.add(ob);
                    }
                    break;
                case "2":
                    System.out.println("Introduceti prenumele");
                    String firstName = myObj.nextLine();  // Read user input
                    String query2 = "SELECT * FROM   users WHERE first_name=" + "'" + firstName + "'";
                    ResultSet rs2 = stmt.executeQuery(query2);
                    while (rs2.next()) {
                        int id = rs2.getInt(1);
                        String lastname = rs2.getString(2);
                        String firstname = rs2.getString(3);
                        String email = rs2.getString(4);
                        String gender = rs2.getString(5);
                        Date input = rs2.getDate(6);
                        System.out.println(input);
                        LocalDate birthdate = input.toLocalDate();
                        String password = rs2.getString(7);
                        User ob = new User(id, lastname, firstname, email, gender, birthdate, password);
                        listaUser.add(ob);
                    }
                    break;
                case "3":
                    System.out.println("Introduceti email");
                    String email = myObj.nextLine();  // Read user input
                    String query3 = "SELECT * FROM   users WHERE email=" + "'" + email + "'";
                    ResultSet rs3 = stmt.executeQuery(query3);
                    while (rs3.next()) {
                        int id = rs3.getInt(1);
                        String lastname = rs3.getString(2);
                        String firstname = rs3.getString(3);
                        String emails = rs3.getString(4);
                        String gender = rs3.getString(5);
                        Date input = rs3.getDate(6);
                        System.out.println(input);
                        LocalDate birthdate = input.toLocalDate();
                        String password = rs3.getString(7);
                        User ob = new User(id, lastname, firstname, email, gender, birthdate, password);
                        listaUser.add(ob);
                    }
                    break;

            }
            System.out.println(listaUser.size());
            return listaUser;
        }
    }


    public static List<User> my_search(String criteriu,String to_search){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Utils.connectionString, Utils.user, Utils.password);
            Statement stmt = conn.createStatement();
            List<User> listaUser = new ArrayList<>();
            switch (criteriu) {
                case "1" -> {
                    String query = "SELECT * FROM   users WHERE last_name=" + "'" + to_search + "'";
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int id = rs.getInt("id_user");
                        String lastname = rs.getString("last_name");
                        String firstname = rs.getString("first_name");
                        String email = rs.getString("email");
                        String gender = rs.getString("gender");
                        LocalDate birthdate = rs.getDate("birth_date").toLocalDate();
                        User ob = new User(id, lastname, firstname, email, gender, birthdate, null);
                        listaUser.add(ob);
                    }
                }
                case "2" -> {
                    String query2 = "SELECT * FROM   users WHERE first_name=" + "'" + to_search + "'";
                    ResultSet rs2 = stmt.executeQuery(query2);
                    while (rs2.next()) {
                        int id = rs2.getInt("id_user");
                        String lastname = rs2.getString("last_name");
                        String firstname = rs2.getString("first_name");
                        String email = rs2.getString("email");
                        String gender = rs2.getString("gender");
                        LocalDate birthdate = rs2.getDate("birth_date").toLocalDate();
                        User ob = new User(id, lastname, firstname, email, gender, birthdate, null);
                        listaUser.add(ob);
                    }
                }
                case "3" -> {
                    String query3 = "SELECT * FROM   users WHERE email=" + "'" + to_search + "'";
                    ResultSet rs3 = stmt.executeQuery(query3);
                    while (rs3.next()) {
                        int id = rs3.getInt("id_user");
                        String lastname = rs3.getString("last_name");
                        String firstname = rs3.getString("first_name");
                        String email = rs3.getString("email");
                        String gender = rs3.getString("gender");
                        LocalDate birthdate = rs3.getDate("birth_date").toLocalDate();
                        User ob = new User(id, lastname, firstname, email, gender, birthdate, null);
                        listaUser.add(ob);
                    }
                }
                default -> {
                    return null;
                }

            }
            return listaUser;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return null;
    }

    public String getCompleteName(){
        return firstName+" "+lastName;
    }
    public String getCompleteSearch(){
        return firstName+" "+lastName+" "+email;
    }


    public static void main(String[] args) throws SQLException {
        System.out.println(my_search("2","bb"));
    }
}