import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        System.out.println("^^^^^^^^^^Welcome to DAT17C/Jonas Chat System^^^^^^^^^^^\n\nType HELP for list of commands");

        //implement receive messages

        Socket socket = new Socket();
        Scanner sc = new Scanner(System.in);
        String line;
        Thread heartBeater = null;
        Thread receiver = null;

        do {
            line = sc.nextLine();
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

                        if (socket.isConnected()) {
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
                        }
                        heartBeater.start();
                        //heartBeater.join();

                    } else {
                        System.out.println("Username is malformed:\n Please enter new username with with letters, digits, underscore or hyphen.\n Must not be longer than 12 characters.");
                    }


                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (line.equals("QUIT")) {
                if (heartBeater != null) {
                    heartBeater.interrupt();
                }
                if (receiver != null) {
                    receiver.interrupt();
                }
                socket.close();
            } else {
                OutputStream toServer = socket.getOutputStream();
                sendMessage(toServer, line);
            }
        }
        while (!line.equals("QUIT"));



  /*      System.out.print("What is the IP for the server (type 0 for localhost): ");
        String ipToConnect = args.length >= 1 ? args[0] : sc.nextLine();

        System.out.print("What is the PORT for the server: ");
        int portToConnect = args.length >= 2 ? Integer.parseInt(args[1]) : sc.nextInt();


        final int PORT_SERVER = portToConnect;
        final String IP_SERVER_STR = ipToConnect.equals("0") ? "127.0.0.1" : ipToConnect;*/

        /*try {
            InetAddress ip = InetAddress.getByName(IP_SERVER_STR);

            System.out.println("\nConnecting...");
            System.out.println("SERVER IP: " + IP_SERVER_STR);
            System.out.println("SERVER PORT: " + PORT_SERVER + "\n");

            Socket socket = new Socket(ip, PORT_SERVER);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            sc = new Scanner(System.in);
            System.out.println("What do you want to send? ");
            String msgToSend = sc.nextLine();

            byte[] dataToSend = msgToSend.getBytes();
            output.write(dataToSend);

            byte[] dataIn = new byte[1024];
            input.read(dataIn);
            String msgIn = new String(dataIn);
            msgIn = msgIn.trim();


            System.out.println("IN -->" + msgIn + "<--");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
