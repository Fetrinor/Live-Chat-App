package com.example.chat_mobil_final.model;

public class Chat {

    private String messageContent, sendUser, reciveUser, messagetype, docID;

    public Chat(String messageContent, String sendUser, String reciveUser, String messagetype, String docID) {
        this.messageContent = messageContent;
        this.sendUser = sendUser;
        this.reciveUser = reciveUser;
        this.messagetype = messagetype;
        this.docID = docID;
    }

    public Chat(){

    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getReciveUser() {
        return reciveUser;
    }

    public void setReciveUser(String reciveUser) {
        this.reciveUser = reciveUser;
    }

    public String getMessagetype() {
        return messagetype;
    }

    public void setMessagetype(String messagetype) {
        this.messagetype = messagetype;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
