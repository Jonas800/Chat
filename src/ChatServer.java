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
                System.out.println("IP: " + clientIp);
                System.out.println("PORT: " + socket.getPort());

                String username = "";

                try {
                    InputStream input = socket.getInputStream();
                    OutputStream output = socket.getOutputStream();

                    byte[] dataIn = new byte[1024];
                    input.read(dataIn);
                    String msgIn = new String(dataIn);
                    msgIn = msgIn.trim();
                    //System.out.println(msgIn);

                    if (msgIn.contains("JOIN")) {
                        Client client = new Client();

                        int indexOfComma = msgIn.lastIndexOf(",");
                        username = msgIn.substring(5, indexOfComma);

                        String welcomeMessage = username + " has joined the chat";
                        sendMessage(output, welcomeMessage);
                        System.out.println(username + ": " + msgIn);

                        client.setIp(socket.getInetAddress().getHostAddress());
                        client.setUsername(username);
                        client.setSocket(socket);
                        client.setInput(socket.getInputStream());
                        client.setOutput(socket.getOutputStream());
                        clients.add(client);

                        ArrayList<Thread> receivers = new ArrayList<>();
                        Thread receiver = new Thread(() -> {
                            while (true) {
                                try {
                                    InputStream inputStream = client.getInput();
                                    byte[] dataFromClient = new byte[1024];
                                    inputStream.read(dataFromClient);
                                    String msgFromClient = new String(dataFromClient);
                                    msgFromClient = msgFromClient.trim();

                                    if (msgFromClient.equals("QUIT")) {
                                        socket.close();
                                        break;
                                    } else if (msgFromClient.equals("IMAV")) {
                                        client.setSecondsSinceLastHeartbeat(0);
                                    } else {

                                        String msgToAllClients = client.getUsername() + ": " + msgFromClient;
                                        System.out.println(msgToAllClients);
                                        for (Client c : clients) {
                                            sendMessage(c.getOutput(), msgToAllClients);
                                        }
                                    }
                                    if (client.getSecondsSinceLastHeartbeat() > 10) {
                                        socket.close();
                                        break;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        receivers.add(receiver);

                        Thread heartbeatIncrementer = new Thread(() -> {
                            while (true) {
                                client.incrementHeartbeat();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(client.getSecondsSinceLastHeartbeat());
                            }
                        });
                        heartbeatIncrementer.start();

                        for (Thread t : receivers) {
                            t.start();
                        }
                        for (Thread t : receivers) {
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (msgIn.equals("IMAV")) {
                        //do nothing?
                        //System.out.println("test");
                        //remove this
                    } else {
                        //???????
                        //String msgToSend = username + ": " + msgIn;
                        //System.out.println(msgToSend);
                        //sendMessage(output, msgToSend);
                        //Send error msg
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
}
