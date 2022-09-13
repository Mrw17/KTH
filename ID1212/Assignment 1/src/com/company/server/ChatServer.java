package com.company.server;

import com.company.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that will represents a chat server
 */
public class ChatServer implements Listener {
    private static int MAX_USERS = 5;
    private static String IP = "localhost";
    private static int PORT = 1237;
    private static String SERVER_NAME = "Server";
    private List<Client> clients = new ArrayList<>();

    /**
     * Starts the chatserver
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        try {
            chatServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new client connection when a client tries to connect.
     * Sends welcome message to new clients
     * creates listner on new messages that will flood them to the other clients
     * @throws Exception
     */
    private void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port 1234");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Client client = new Client(clientSocket, this);
            client.start();
            Message m = getWelcomeMessage();
            client.sendMessage(m);
            client.addListener(this);
            clients.add(client);
        }
    }

    /**
     * Creates a welcome message that is show to new clients
     * @return
     */
    private Message getWelcomeMessage(){
        return new Message(SERVER_NAME, "Welcome to chatroom!");
    }

    /**
     * Floods a message to all chat clients except the one that send it.
     * @param message
     * @param clientThatSend
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void floodNewMessage(Message message, Client clientThatSend) throws IOException, ClassNotFoundException {
        for (Client c : clients) {
            if (c != clientThatSend)
                c.sendMessage(message);
        }
    }
}