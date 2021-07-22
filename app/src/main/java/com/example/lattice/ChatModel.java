package com.example.lattice;

public class ChatModel {

    public static final int SEND = 0;
    public static final int RECEIVE = 1;


    private String message;
    private int sendReceive;

    public ChatModel(String message, int sendReceive) {
        this.message = message;
        this.sendReceive = sendReceive;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSendReceive() {
        return sendReceive;
    }

    public void setSendReceive(int sendReceive) {
        this.sendReceive = sendReceive;
    }


}
