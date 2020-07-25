package com.android.wefour.classmanager.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
//class for group chat
public class GroupChat {

    private String groupId;
    private Map<String, ChatMessage> messageMap;

    public GroupChat() {
    }

    public GroupChat(String groupId, Map<String, ChatMessage> messageMap) {
        this.groupId = groupId;
        this.messageMap = messageMap;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, ChatMessage> getMessageMap() {
        return messageMap;
    }

    public void setMessageMap(Map<String, ChatMessage> messageMap) {
        this.messageMap = messageMap;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupId",groupId);
        result.put("messageMap", messageMap);
        return result;
    }
}
