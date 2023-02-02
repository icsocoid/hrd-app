package com.sia.als.model;

public class Pengajuan {
    private String id;
    private String nama_izin;
    private String tanggal;
    private String als_hrm_izin_id;
    private String tanggal_awal;
    private String tanggal_akhir;
    private String photo;
    private String latitude;
    private String longitude;
    private String als_employee_id;
    private String keterangan;
    private String status_izin;
    private String namaKaryawan;

    public Pengajuan()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_izin() {
        return nama_izin;
    }

    public void setNama_izin(String nama_izin) {
        this.nama_izin = nama_izin;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getAls_hrm_izin_id() {
        return als_hrm_izin_id;
    }

    public void setAls_hrm_izin_id(String als_hrm_izin_id) {
        this.als_hrm_izin_id = als_hrm_izin_id;
    }

    public String getTanggal_awal() {
        return tanggal_awal;
    }

    public void setTanggal_awal(String tanggal_awal) {
        this.tanggal_awal = tanggal_awal;
    }

    public String getTanggal_akhir() {
        return tanggal_akhir;
    }

    public void setTanggal_akhir(String tanggal_akhir) {
        this.tanggal_akhir = tanggal_akhir;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public String getAls_employee_id() {
        return als_employee_id;
    }

    public void setAls_employee_id(String als_employee_id) {
        this.als_employee_id = als_employee_id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getStatus_izin() {
        return status_izin;
    }

    public void setStatus_izin(String status_izin) {
        this.status_izin = status_izin;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }
}
