package yourteamnumber.seshealthpatient.Model;

public class User {
    String Password;
    String Username;
    String UserType;

    public User(){
    }

    public User(String Password, String Username){
        this.Password = Password;
        this.Username = Password;
        this.UserType = UserType;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public boolean isDoctor() {
        if(UserType == "Doctor") {
            return true;
        }
        else{
            return false;
        }
    }

}
