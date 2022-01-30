package com.example.chat_mobil_final.model;

public class User {
    private String UserName;
    private String Email;
    private String UserID;
    private String UserProfile;

    public User(String userName, String email, String userID, String userProfile) {
        UserName = userName;
        Email = email;
        UserID = userID;
        UserProfile = userProfile;
    }

    public User(){}

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserProfile() {
        return UserProfile;
    }

    public void setUserProfile(String userProfile) {
        UserProfile = userProfile;
    }
}
