package models;

import org.apache.commons.validator.routines.EmailValidator;

public interface Utils {
    String connectionString = "jdbc:mysql://127.0.0.1:3306/chat";
    String user="localuser";
    String password="123456";
    String fileServerURL="http://localhost:3000/";
    static boolean match_mail(String email){
        return EmailValidator.getInstance().isValid(email);
    }
}
