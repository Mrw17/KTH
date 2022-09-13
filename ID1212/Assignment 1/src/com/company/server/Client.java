package com.company.server;

import com.company.common.Communication;
import com.company.common.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

interface Listener {
    void floodNewMessage(Message message, Client client) throws IOException, ClassNotFoundException;
}

public class Client extends Thread{
    private ChatServer chatServer;
    private Communication conn;

    private List<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener toAdd) {
        listeners.add(toAdd);
    }

    public Client(Socket socket, ChatServer chatServer){
        this.chatServer = chatServer;
        this.conn = new Communication(socket);
    }

    public void run(){
        Message message;
        try{
            while((message = conn.readMessage()) != null){
                printMessage(message);

                System.out.println("test1");
                for (Listener hl : listeners) {
                    System.out.println("test");
                    hl.floodNewMessage(message, this);
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void printMessage(Message message){
        System.out.println(message.getChatname() + ": " + message.getMessage());
    }

    public void sendMessage(Message message) throws IOException, ClassNotFoundException {
        conn.sendMessage(message);



    }

}
