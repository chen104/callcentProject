package com.example.vmac.callCenter;

/**
 * Created by VMac on 17/11/16.
 */

import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Table(name = "message")
public class Message implements Serializable {
    public String id, message;
    public String action;
    public Message() {
        this.id="1";
    }
    public int msgType;
    public Object optional;
    public boolean needRead=true;

    public void setOptional(Object optional) {
        this.optional = optional;
    }

    public Object getOptional() {
        return optional;
    }

    public Message(String id, String message, String createdAt) {
        this.id = id;
        this.message = message;


    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

