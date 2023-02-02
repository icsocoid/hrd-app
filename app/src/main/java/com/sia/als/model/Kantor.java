package com.sia.als.model;

public class Kantor {
    private String id;
    private String namaKantor;
    private String alamat;
    private String latitude;
    private String longitude;
    private String statusKantor;

    public Kantor()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaKantor() {
        return namaKantor;
    }

    public void setNamaKantor(String namaKantor) {
        this.namaKantor = namaKantor;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatusKantor() {
        return statusKantor;
    }

    public void setStatusKantor(String statusKantor) {
        this.statusKantor = statusKantor;
    }
}
