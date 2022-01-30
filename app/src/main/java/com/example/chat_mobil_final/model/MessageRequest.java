package com.example.chat_mobil_final.model;

public class MessageRequest {
    private String chanelID;
    private String targetID;
    private String UserName;
    private String UserProfile;

    public MessageRequest(String chanelID, String targetID, String UserName, String UserProfile) {
        this.chanelID = chanelID;
        this.targetID = targetID;
        this.UserName = UserName;
        this.UserProfile = UserProfile;
    }

    public MessageRequest(){
    }

    public String getChanelID() {
        return chanelID;
    }

    public void setChanelID(String kanalID) {
        this.chanelID = kanalID;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserProfile() {
        return UserProfile;
    }

    public void setUserProfile(String userProfile) {
        UserProfile = userProfile;
    }
}
