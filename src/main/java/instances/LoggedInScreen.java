package instances;

import models.Group;
import models.User;

import java.util.ArrayList;
import java.util.Scanner;

public class LoggedInScreen {
    static User user;
    static ArrayList<Group> groups;

    public static void setUser(User user) {
        LoggedInScreen.user = user;
        groups = Group.getGroups(user.getIdUser());
    }

    public static User getUser() {
        return user;
    }

    public static void screen(){
        if(user!=null) {
            boolean ok = true;
            while(ok){
                System.out.println("1. View groups");
                System.out.println("2. View Profile");
                System.out.println("0. Exit");
                System.out.print("Option: ");
                String option = new Scanner(System.in).nextLine();
                switch(option){
                    case "1" ->{
                        for (int i = 0; i < groups.size(); i++)
                            System.out.println(i + " " + groups.get(i).toString());
                        System.out.println("-1 to exit or number of group to acces that group");
                        while(true) {
                            System.out.print("Option: ");
                            String option2 = new Scanner(System.in).nextLine();
                            if (option2.equals("-1")) {
                                break;
                            } else {
                                try {
                                    if (Integer.parseInt(option2) >= 0 && Integer.parseInt(option2) < groups.size()) {
                                        GroupScreen.setGroup(groups.get(Integer.parseInt(option2)));
                                        GroupScreen.screen();
                                    } else {
                                        System.out.println("Invalid input");
                                    }
                                } catch(NumberFormatException exception){
                                    System.out.println("Invalid input");
                                }
                            }
                        }
                    }
                    case "0" -> ok=false;
                    case "2" -> {
                        System.out.println("ID: "+user.getIdUser());
                        System.out.println("Name: "+user.getFirstName()+" "+user.getLastName());
                        System.out.println("Email: "+user.getEmail());
                        System.out.println("Gender: "+user.getGender());
                        System.out.println("Birthdate: "+user.getBirthDate());
                    }
                    default -> System.out.println("Invalid option");
                }


            }
        }
        LoggedInScreen.user=null;
    }
}
