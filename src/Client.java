import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket socket = new Socket();
    private InputStream input;
    private OutputStream output;
    private String ip;
    private String username;
    private int secondsSinceLastHeartbeat;

    public Client() {
    }

    public Socket getSocket() {
        return socket;
    }

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ip;
    }

    public synchronized void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public int getSecondsSinceLastHeartbeat() {
        return secondsSinceLastHeartbeat;
    }

    public void incrementHeartbeat(){
        secondsSinceLastHeartbeat++;
    }

    public void setSecondsSinceLastHeartbeat(int secondsSinceLastHeartbeat) {
        this.secondsSinceLastHeartbeat = secondsSinceLastHeartbeat;
    }
}
