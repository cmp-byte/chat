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
                System.out.println("0. Exit");
                System.out.print("Option: ");
                String option = new Scanner(System.in).nextLine();
                if(option.equals("1")){
                    for(int i=0;i<groups.size();i++)
                        System.out.println(i+" "+ groups.get(i).toString());
                    System.out.println("-1 to exit or number of group to acces that group");
                    option=new Scanner(System.in).nextLine();
                    if(option.equals("-1")){
                        return;
                    } else {
                        if(Integer.parseInt(option)>=0 && Integer.parseInt(option)<groups.size()){
                            GroupScreen.setGroup(groups.get(Integer.parseInt(option)));
                            GroupScreen.screen();
                        } else {
                            System.out.println("Invalid input");
                        }
                    }
                } else if(option.equals("0")){
                    ok=false;
                } else{
                    System.out.println("Invalid option");
                }
            }
        }
    }
}
