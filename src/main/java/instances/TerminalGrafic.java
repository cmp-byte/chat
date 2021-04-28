package instances;

import models.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TerminalGrafic {

    public static void main(String[] args) {
        User user = null;
        boolean ok = true;
        while(ok){
            System.out.println("================MENU================");
            System.out.println("1. Log In");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Option: ");
            String optiune = new Scanner(System.in).nextLine();
            // if in loc de switch pentru ca in Java switch-ul nu e ca in C++
            if(optiune.equals("1")){
                System.out.print("Email: ");
                String email = new Scanner(System.in).nextLine();
                System.out.print("Password: ");
                String password = new Scanner(System.in).nextLine();
                try {
                    user = User.my_login(email,password);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    System.out.println("Conexiunea la baza de date a esuat");
                }
                if(user!=null){
                    LoggedInScreen.setUser(user);
                    LoggedInScreen.screen();
                } else {
                    System.out.println("Unsuccesfull");
                }
            } else if(optiune.equals("2")){
                System.out.print("Enter LastName: ");
                String lastName = new Scanner(System.in).next();  // Read user input
                System.out.print("Enter FirstName: ");
                String firstName = new Scanner(System.in).next();  // Read user input
                System.out.print("Enter Email: ");
                String email = new Scanner(System.in).next();  // Read user input
                System.out.print("Enter Gender: ");
                String gender = new Scanner(System.in).next();  // Read user input
                System.out.print("Enter BirthDate(d/MM/yyyy): ");
                LocalDate birthDate = LocalDate.parse(new Scanner(System.in).nextLine(), DateTimeFormatter.ofPattern("d/MM/yyyy"));  // Read user input
                System.out.print("Enter Password: ");
                String password= new Scanner(System.in).nextLine();  // Read user input
                try {
                    if(User.my_signup(lastName,firstName,email,gender,birthDate,password)){
                        System.out.println("Succesfull");
                    } else System.out.println("Unsuccessfull");
                } catch (SQLException throwables) {
                    System.out.println("Conexiunea la baza de date a esuat, nereusit");
                }
            } else if(optiune.equals("3")){
                ok=false;
            } else{
                System.out.println("Optiune incorecta");
            }
            System.out.println("\n\n\n\nPress ENTER to continue");
            new Scanner(System.in).nextLine();

        }
    }
}
