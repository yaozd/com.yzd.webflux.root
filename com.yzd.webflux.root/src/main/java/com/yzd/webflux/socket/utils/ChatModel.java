package com.yzd.webflux.socket.utils;

import java.io.Serializable;

public class ChatModel implements Serializable {
   private String type;
   private String message;
   private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
