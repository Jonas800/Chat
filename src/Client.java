import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private String ip;
    private String username;
    private int secondsSinceLastHeartbeat;
    private boolean isConnected = true;
    private Thread heartbeatIncrementer = new Thread(() -> {
        while (isConnected()) {
            try {
                incrementHeartbeat();
                if (getSecondsSinceLastHeartbeat() > 10) {
                    setConnected(false);
                    break;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getSecondsSinceLastHeartbeat());
        }
    });

    public Client() {
        heartbeatIncrementer.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
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

    public synchronized int getSecondsSinceLastHeartbeat() {
        return secondsSinceLastHeartbeat;
    }

    public synchronized void incrementHeartbeat(){
        secondsSinceLastHeartbeat++;
    }

    public synchronized void setSecondsSinceLastHeartbeat(int secondsSinceLastHeartbeat) {
        this.secondsSinceLastHeartbeat = secondsSinceLastHeartbeat;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Thread getHeartbeatIncrementer() {
        return heartbeatIncrementer;
    }
}
