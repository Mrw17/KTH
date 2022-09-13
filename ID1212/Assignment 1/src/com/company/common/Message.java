package com.company.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Class that represents a message
 */
public class Message implements Serializable {

    String name = "";
    String message = "";

    /**
     * Creates a message with given input
     * @param name
     * @param message
     */
    public Message(String name, String message) {
        this.name = name;
        this.message = message;
    }

    /**
     * Returns the message
     * @return
     */
    public String getMessage(){
        return this.message;
    }

    /**
     * Returns the chatname
     * @return
     */
    public String getChatname(){
        return this.name;
    }



}
