import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        System.out.println("^^^^^^^^^^Welcome to DAT17C/Jonas Chat System^^^^^^^^^^^\n\nType HELP for list of commands");

        Socket socket = new Socket();
        Scanner sc = new Scanner(System.in);
        String line = "";
        Thread heartBeater = null;
        Thread receiver = null;

        do {
            if (line.equals("QUIT") == false) {
                line = sc.nextLine();
            }
            if (line.equals("HELP")) {
                System.out.println("JOIN <<user_name>>, <<server_ip>>:<<server_port>>\nDATA <<user_name>>: <<free text…>>\nQUIT\n");
            }
            if (line.contains("JOIN")) {
                try {
                    int indexOfComma = line.lastIndexOf(",");
                    int indexOfColon = line.lastIndexOf(":");
                    String username = line.substring(5, indexOfComma);
                    //System.out.println(username);

                    if (username.matches("^[a-zA-Z\\d-_]{0,12}$")) {
                        String server_ip = line.substring(indexOfComma + 2, indexOfColon);
                        //System.out.println(server_ip);
                        int server_port = Integer.parseInt(line.substring(indexOfColon + 1));

                        InetAddress ip = InetAddress.getByName(server_ip);

                        System.out.println("\nConnecting...");
                        System.out.println("SERVER IP: " + server_ip);
                        System.out.println("SERVER PORT: " + server_port + "\n");

                        //establish connection
                        socket = new Socket(ip, server_port);


                        System.out.println("Connection reached");
                        OutputStream toServer = socket.getOutputStream();
                        InputStream fromServer = socket.getInputStream();

                        //Send JOIN command
                        sendMessage(toServer, line + "\n");

                        receiver = new Thread(() -> {
                            try {
                                while (true) {
                                    byte[] dataIn = new byte[1024];
                                    fromServer.read(dataIn);
                                    String msgIn = new String(dataIn);
                                    msgIn = msgIn.trim();
                                    System.out.println(msgIn);

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        receiver.start();

                        //Send heartbeats
                        heartBeater = new Thread(() -> {
                            try {
                                String heartbeat = "IMAV";
                                while (true) {
                                    Thread.sleep(60000);
                                    sendMessage(toServer, heartbeat);
                                    //System.out.println(heartbeat);
                                }
                            } catch (InterruptedException e) {
                                //e.printStackTrace();
                            }
                        });
                        heartBeater.start();
                    } else {
                        System.out.println("Username is malformed:\n Please enter new username with with letters, digits, underscore or hyphen.\n Must not be longer than 12 characters.");
                    }
                } catch (StringIndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                }

            } else if (line.equals("QUIT")) {
                OutputStream toServer = socket.getOutputStream();
                sendMessage(toServer, line);
            } else {
                OutputStream toServer = socket.getOutputStream();
                sendMessage(toServer, line);
            }
        }
        while (!line.equals("QUIT"));
        heartBeater.interrupt();
        receiver.interrupt();
        socket.close();
        System.out.println("Shutting down chat...");
    }

    public static void sendMessage(OutputStream output, String message) {
        try {
            byte[] dataToSend = message.getBytes();
            output.write(dataToSend);
            output.flush();
        } catch (IOException e) {
            System.err.println("MESSAGE NOT SENT");
        }
    }
}
