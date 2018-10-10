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
            } else {


                if (line.contains("JOIN")) {
                    try {
                        int indexOfComma = line.lastIndexOf(",");
                        int indexOfColon = line.lastIndexOf(":");
                        String username = line.substring(5, indexOfComma);
                        //System.out.println(username);

                        //if (username.matches("^[a-zA-Z\\d-_]{0,12}$")) {
                        String server_ip = line.substring(indexOfComma + 2, indexOfColon);
                        //System.out.println(server_ip);
                        int server_port = Integer.parseInt(line.substring(indexOfColon + 1));

                        InetAddress ip = InetAddress.getByName(server_ip);

                        System.out.println("\nConnecting...");
                        System.out.println("SERVER IP: " + server_ip);
                        System.out.println("SERVER PORT: " + server_port);

                        //establish connection
                        socket = new Socket(ip, server_port);


                        System.out.println("Connection reached...");
                        OutputStream toServer = socket.getOutputStream();
                        InputStream fromServer = socket.getInputStream();

                        //Send JOIN command
                        sendMessage(toServer, line + "\n");

                        String connectionAcknowledgement = recieveMessage(fromServer);
                        if (connectionAcknowledgement.equals("J_OK")) {

                            receiver = new Thread(() -> {
                                while (true) {
                                    String msgIn = recieveMessage(fromServer);
                                    if (msgIn.equals("IOException")) {
                                        System.err.println("Connection terminated");
                                        break;
                                    } else {
                                        System.out.println(msgIn);
                                    }
                                }
                            });
                            receiver.start();

                            //Send heartbeats
                            heartBeater = new Thread(() -> {
                                while (true) {
                                    try {
                                        String heartbeat = "IMAV";
                                        Thread.sleep(60000);
                                        //sendMessage(toServer, heartbeat);
                                        byte[] dataToSend = heartbeat.getBytes();
                                        toServer.write(dataToSend);
                                        toServer.flush();

                                        //System.out.println(heartbeat);
                                    } catch (InterruptedException e) {
                                        //e.printStackTrace();
                                        System.err.println("Connection terminated");
                                        break;
                                    } catch (IOException e) {
                                        //System.out.println("hb stop");
                                        break;
                                    }
                                }
                            });
                            heartBeater.start();
                        } else {
                            System.out.println("SERVER: " + connectionAcknowledgement);
                            socket.close();
                        }
                        //} //else {
                        //System.out.println("Username is malformed:\n Please enter new username with with letters, digits, underscore or hyphen.\n Must not be longer than 12 characters.");
                        //}
                    } catch (StringIndexOutOfBoundsException e) {
                        System.err.println("Unknown JOIN command");
                    } catch (IOException e) {
                        System.err.println("Connection failed");
                    }

                } else if (line.equals("QUIT")) {
                    try {
                        OutputStream toServer = socket.getOutputStream();
                        sendMessage(toServer, line);
                    } catch (SocketException e) {
                        //Just shut down
                    }
                } else {
                    try {
                        OutputStream toServer = socket.getOutputStream();
                        sendMessage(toServer, line);
                    } catch (SocketException e) {
                        System.err.println("Not connected to a server");
                    }
                }
            }
        }
        while (!line.equals("QUIT"));
        if (heartBeater != null) {
            heartBeater.interrupt();
        }
        if (receiver != null) {
            receiver.interrupt();
        }
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

    public static String recieveMessage(InputStream input) {
        String message = "";
        try {
            byte[] dataIn = new byte[1024];
            input.read(dataIn);
            String msgIn = new String(dataIn);
            message = msgIn.trim();
            //System.out.println(msgIn);
        } catch (IOException e) {
            //System.err.println("Connection terminated");
            message = "IOException";
        }
        return message;
    }
}
