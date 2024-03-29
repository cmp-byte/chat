package instances;

import models.Group;
import models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoggedInScreen {
    static User user;
    static ArrayList<Group> groups = new ArrayList<>();

    public static void setUser(User user) {
        LoggedInScreen.user = user;
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
                System.out.println("3. Create group");
                System.out.println("4. Search User");
                System.out.println("0. Exit");
                System.out.print("Option: ");
                String option = new Scanner(System.in).nextLine();
                switch(option){
                    case "1" ->{
                        while(true) {
                            groups = Group.getGroups(user.getIdUser());
                            for (int i = 0; i < groups.size(); i++)
                                System.out.println(i + " " + groups.get(i).toString());
                            System.out.println("-1 to exit or number of group to acces that group");
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
                    case "3" -> {
                        User user2 = new User();
                        int id;
                        while(true){
                            try{
                                System.out.print("Enter ID for the User you wish to start a conversation: ");
                                id = Integer.parseInt(new Scanner(System.in).nextLine());
                                break;
                            } catch(NumberFormatException exception){
                                System.out.println("Input is wrong");
                            }
                        }
                        user2.setIdUser(id);
                        Group group = Group.create(user,user2);
                    }
                    case "4" ->{
                        System.out.print("Search by: 1(last_name),2(first_name),3(email)");
                        String criteriu = new Scanner(System.in).nextLine();
                        System.out.print("What to search: ");
                        List<User> users = User.my_search(criteriu,new Scanner(System.in).nextLine());
                        if(users!=null){
                            System.out.println("Found "+users.size()+" results");
                            for(int i=0;i<users.size();i++){
                                System.out.println(users.get(i).getIdUser()+" "+users.get(i).getFirstName()+" "+users.get(i).getLastName()+" "+users.get(i).getEmail());
                            }
                        } else {
                            System.out.println("There was an error.");
                        }

                    }
                    default -> System.out.println("Invalid option");
                }


            }
        }
        LoggedInScreen.user=null;
    }

}
