import java.lang.reflect.Array;
import java.util.ArrayList;

public class UsernameChecker {

    private static UsernameChecker instance = new UsernameChecker();
    private String code;
    private String message;
    private boolean isOK;
    private static ArrayList<Client> clients;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void checkUsername(String username){
        checkUsername(username, clients);
    }

    public void checkUsername(String username, ArrayList<Client> clients) {
        if (username.matches("^[a-zA-Z\\d-_]{0,12}$")) {
            isOK = true;
            message = "J_OK";
        } else{
            isOK = false;
            message = "J_ER 99: Username is malformed:\n Please enter new username with with letters, digits, underscore or hyphen.\n Must not be longer than 12 characters.";
            System.out.println(message);
        }

        for (Client c : clients) {
            if (c.getUsername().equals(username)) {
                isOK = false;
                message = "J_ER 111: Username is taken";
                System.out.println(message);
            }
        }
    }

    public static UsernameChecker getInstance(ArrayList<Client> clients) {
        UsernameChecker.clients = clients;
        return instance;
    }

}
