package com.android.wefour.classmanager.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;
//class for chat message
public class ChatMessage {

    private String message;
    private String messageId;
    private String type;
    private String fromId;
    private String fromName;
    private Object timestamp;

    public ChatMessage() {

    }

    public ChatMessage(String message, String messageId, String from_id, String from_name, String type) {
        this.message = message;
        this.fromId = from_id;
        this.fromName = from_name;
        this.messageId = messageId;
        this.type = type;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message",message);
        result.put("messageId",messageId);
        result.put("fromId",fromId);
        result.put("fromName",fromName);
        result.put("timestamp", timestamp);
        result.put("type",type);
        return result;
    }

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

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String from_id) {
        this.fromId = from_id;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String from_name) {
        this.fromName = from_name;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
