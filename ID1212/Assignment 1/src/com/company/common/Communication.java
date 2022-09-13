package com.company.common;

import java.io.*;
import java.net.Socket;

/**
 * Class that will put out/receive message-object from sockets
 */
public class Communication {
    Socket socket;

    public Communication(Socket socket){
        this.socket = socket;
    }

    /**
     * Will retrieve message-object from socket.
     * @return message-object from socket.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Message readMessage() throws IOException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream  = new ObjectInputStream(inputStream);
        Message message = (Message) objectInputStream.readObject();
        return message;
    }

    /**
     * Will send a message-object to socket.
     * @param message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendMessage(Message message)throws IOException, ClassNotFoundException{
        OutputStream outputStream = null;
        outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(message);
    }
}