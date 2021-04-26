package models;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Group implements IGroup {
    private int idGroup = 0;
    private String title;
    private ArrayList<User> users;
    private List<Message> messages;

    public Group() {
        // constructor fara paramentrii , util pt testarea functiilor din clasa
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
            if (this.users.size() >= 2)
                this.title = this.users.get(0).getFirstName() + "_" + this.users.get(1).getFirstName();// conform cerintei titlul implicit va fi format din numele celor 2 participanti
                // this.users.get(0).getFirstName e echivalentul in c++ pentru un vector de genul this->Vector[0].getWhatever();

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

    @Override
    public boolean add(User user) throws java.sql.SQLException {
        //Adaugam in vectorul de participanti unul nou
        users.add(user);

        //Ce inseamna sa adaug inca o persoana conversatiei/grupului?
        // Inseamna sa ii crez o legatura user_group in baza de date, deci asta vom face cu aceasta functie.
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "Cmp", "1237"); //deschid conexiunea
        String query = "insert into chat.user_group (id_user,id_group) values(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user.getIdUser());
        preparedStatement.setInt(2, this.idGroup);
        preparedStatement.executeUpdate();
        connection.close();
        System.out.println("User adaugat cu succes!");
        return true;
    }

    @Override
    public boolean delete() throws java.sql.SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "Cmp", "1237"); //deschid conexiunea
        String query = "delete from chat.groups where id_group=?";
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

    public static void main(String[] args) throws IOException, java.sql.SQLException {
        //zona de test
        /*Group group = new Group();
        group.setIdGroup(3);
        group.rename();*/
        User user1 = new User();
        user1.setFirstName("Test");
        User user2 = new User();
        user2.setFirstName("Proba");

        ArrayList<User> users = new ArrayList<User>();
        users.add(user1);
        users.add(user2);


        Group group = new Group(users);

    }

}
