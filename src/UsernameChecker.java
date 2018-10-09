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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void checkUsername(String username) {
        if (username.matches("^[a-zA-Z\\d-_]{0,12}$")) {
            isOK = true;
            message = "J_OK";
        } else{
            isOK = false;
            message = "J_ER 99: Username is malformed";
        }

        for (Client c : clients) {
            if (c.getUsername().equals(username)) {
                isOK = false;
                message = "J_ER 111: Username is taken";
            }
        }
    }

    public static UsernameChecker getInstance(ArrayList<Client> clients) {
        UsernameChecker.clients = clients;
        return instance;
    }

}
