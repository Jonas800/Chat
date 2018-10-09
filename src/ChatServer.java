import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
    public static void main(String[] args) {
        System.out.println("=============SERVER==============");

        final int PORT_LISTEN = 5656;
        final ArrayList<Client> clients = new ArrayList<>();

        try {
            ServerSocket server = new ServerSocket(PORT_LISTEN);
            System.out.println("Server starting...\n");
            while (true) {

                final Socket socket = server.accept();

                System.out.println("Client connected");
                String clientIp = socket.getInetAddress().getHostAddress();

                String username = "";

                try {
                    InputStream input = socket.getInputStream();

                    byte[] dataIn = new byte[1024];
                    input.read(dataIn);
                    String msgIn = new String(dataIn);
                    msgIn = msgIn.trim();
                    //System.out.println(msgIn);

                    if (msgIn.contains("JOIN")) {

                        int indexOfComma = msgIn.lastIndexOf(",");
                        username = msgIn.substring(5, indexOfComma);

                        System.out.println("IP: " + clientIp);
                        System.out.println("PORT: " + socket.getPort());
                        System.out.println("USERNAME: " + username);

                        String welcomeMessage = "SERVER: " + username + " has joined the chat";
                        sendToAll(clients, welcomeMessage);

                        UsernameChecker usernameChecker = UsernameChecker.getInstance(clients);
                        usernameChecker.checkUsername(username);
                        if (usernameChecker.isOK()) {

                            sendMessage(socket.getOutputStream(), usernameChecker.getMessage());

                            Client client = new Client();
                            client.setConnected(true);
                            client.setIp(socket.getInetAddress().getHostAddress());
                            client.setUsername(username);
                            client.setSocket(socket);
                            client.setInput(socket.getInputStream());
                            client.setOutput(socket.getOutputStream());
                            clients.add(client);

                            ArrayList<Thread> receivers = new ArrayList<>();
                            Thread receiver = new Thread(() -> {
                                while (client.isConnected()) {
                                    try {
                                        InputStream inputStream = client.getInput();
                                        byte[] dataFromClient = new byte[1024];
                                        inputStream.read(dataFromClient);
                                        String msgFromClient = new String(dataFromClient);
                                        msgFromClient = msgFromClient.trim();

                                        if (msgFromClient.contains("JOIN")) {
                                            //don't print anything
                                        } else if (msgFromClient.equals("QUIT")) {
                                            System.out.println("SERVER: " + client.getUsername() + " left the chat");
                                            client.setConnected(false);
                                            break;
                                        } else if (msgFromClient.equals("IMAV")) {
                                            client.setSecondsSinceLastHeartbeat(0);
                                        } else {
                                            String msgToAllClients = client.getUsername() + ": " + msgFromClient;
                                            System.out.println(msgToAllClients);
                                            sendToAll(clients, msgToAllClients);
                                        }
                                        System.out.println(client.isConnected());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            receivers.add(receiver);

                            for (Thread t : receivers) {
                                t.start();
                            }
                        } else{
                            sendMessage(socket.getOutputStream(), usernameChecker.getMessage());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(OutputStream output, String message) {
        try {
            byte[] dataToSend = message.getBytes();
            output.write(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendToAll(ArrayList<Client> clients, String message){
        //check active list here???? don't like it
        for (Client c : clients) {
            if (c.isConnected()) {
                sendMessage(c.getOutput(), message);
            }
        }
    }
}
