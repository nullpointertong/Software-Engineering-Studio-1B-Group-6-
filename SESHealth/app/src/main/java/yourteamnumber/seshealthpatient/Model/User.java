package yourteamnumber.seshealthpatient.Model;

public class User {
    String Password;
    String Username;

    public User(){
    }

    public User(String Password, String Username){
        this.Password = Password;
        this.Username = Password;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

}
