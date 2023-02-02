package com.sia.als.model;

public class Terlambat {
    private String id;
    private String namaAbsensi;
    private String description;
    private int jumlah;

    public Terlambat()
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

    public String getNamaAbsensi() {
        return namaAbsensi;
    }

    public void setNamaAbsensi(String namaAbsensi) {
        this.namaAbsensi = namaAbsensi;
    }
}
