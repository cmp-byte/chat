package instances;

import models.Group;
import models.Message;
import models.User;

import java.sql.SQLException;
import java.util.Scanner;

public class GroupScreen {
    static Group group;

    public static void setGroup(Group group) {
        GroupScreen.group = group;
        group.getNewMessages(0);
    }
    public static void screen(){
        if(group!=null){
            boolean ok=true;
            while(ok) {
                System.out.println("1. View messages");
                System.out.println("2. Get more messages");
                System.out.println("3. Get newest messages");
                System.out.println("4. Send Message without attachement");
                System.out.println("5. Send Message with attachement");
                System.out.println("6. Add user via id");
                System.out.println("7. Rename group");
                System.out.println("0. Exit");
                System.out.print("Option: ");
                String option = new Scanner(System.in).next();
                switch (option) {
                    case "0" ->{
                        ok=false;
                    }
                    case "1" -> {
                        for (Message message : group.getMessages()) {
                            System.out.println(message.getIdUser() + ": " + message.getContentText() + " ||| attached " + message.getAttachment() + " at " + message.get_time_sent());
                        }
                    }
                    case "2" -> {
                        group.getNewMessages();
                    }
                    case "3" ->{
                        group.getNewestMessages();
                    }
                    case "4" -> {
                        String mesaj;
                        System.out.print("Mesaj: ");
                        mesaj = new Scanner(System.in).nextLine();
                        Message message = new Message(LoggedInScreen.getUser().getIdUser(), group.getIdGroup(), mesaj);
                        if (message.send())
                            group.getMessages().add(message);
                        else
                            System.out.println("A aparut o eroare");
                    }
                    case "6" -> {
                        User user = new User();
                        System.out.print("ID user: ");
                        user.setIdUser(Integer.parseInt(new Scanner(System.in).nextLine()));
                        try {
                            group.add(user);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    case "7" ->{
                        String new_name;
                        System.out.print("Insert new name: ");
                        new_name=new Scanner(System.in).nextLine();
                        if(group.my_rename(new_name))
                            System.out.println("Succes!");
                        else
                            System.out.println("No succes!");
                    }
                }
            }
        }
        GroupScreen.group = null;
    }
}
