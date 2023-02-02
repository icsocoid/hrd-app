package com.sia.als.model;

public class Izin {
    private String id;
    private String namaIzin;
    private String description;
    private int jumlah;

    public Izin()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getNamaIzin() {
        return namaIzin;
    }

    public void setNamaIzin(String namaIzin) {
        this.namaIzin = namaIzin;
    }
}
