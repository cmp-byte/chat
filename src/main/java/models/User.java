package models;

import java.time.LocalDate;
import java.util.List;

public class User implements IUser{
    private  int idUser;
    private String lastName;
    private String firstName;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private String password;

    @Override
    public boolean signup() {
        return false;
    }

    @Override
    public boolean login() {
        return false;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public List<User> search() {
        return null;
    }
}
