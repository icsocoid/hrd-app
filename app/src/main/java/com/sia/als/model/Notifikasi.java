package com.sia.als.model;

public class Notifikasi {
    private String id;
    private String userId;
    private String statusNotif;
    //ini subject buat firebase notifikasi
    private String subject;
    //ini ringakasan pada firebase notifikasi
    private String message;
    //konten akan dibuka pake webview
    private String content;
    private String tanggal;
    private String photo;

    public Notifikasi() {

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatusNotif(String statusNotif) {
        this.statusNotif = statusNotif;
    }

    public String getStatusNotif() {
        return statusNotif;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    
}

