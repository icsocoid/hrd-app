package com.sia.als.model;

import android.graphics.Bitmap;

public class Perdin {
    private String id;
//    private String namaKlien;
    private String tanggal;
    private String nomorPerdin;
    private String photoPerdin;
    private Bitmap bitmapPerdin;
    private String catatanPerdin;
    private String statusId;

    public Perdin(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public String getNamaKlien() {
//        return namaKlien;
//    }

//    public void setNamaKlien(String namaKlien) {
//        this.namaKlien = namaKlien;
//    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNomorPerdin() {
        return nomorPerdin;
    }

    public void setNomorPerdin(String nomorPerdin) {
        this.nomorPerdin = nomorPerdin;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getPhotoPerdin() {
        return photoPerdin;
    }

    public void setPhotoPerdin(String photoPerdin) {
        this.photoPerdin = photoPerdin;
    }

    public String getCatatanPerdin() {
        return catatanPerdin;
    }

    public void setCatatanPerdin(String catatanPerdin) {
        this.catatanPerdin = catatanPerdin;
    }

    public Bitmap getBitmapPerdin() {
        return bitmapPerdin;
    }

    public void setBitmapPerdin(Bitmap bitmapPerdin) {
        this.bitmapPerdin = bitmapPerdin;
    }
}
