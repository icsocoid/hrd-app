package com.sia.als.model;

public class PengajuanAbsensi {
    private String id;
    private String jenis_pengajuan;
    private String pengajuan_id;
    private String als_team_employee_id;
    private String tanggal;
    private String tanggal_absensi;
    private String keterangan;
    private String status_pengajuan;
    private String alasan_reject;
    private String status_notif;
    private String nama_karyawan;

    public PengajuanAbsensi(){

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getJenis_pengajuan() {
        return jenis_pengajuan;
    }
    public void setJenis_pengajuan(String jenis_pengajuan) {
        this.jenis_pengajuan = jenis_pengajuan;
    }

    public String getNama_karyawan() {
        return nama_karyawan;
    }
    public void setNama_karyawan(String nama_karyawan) {
        this.nama_karyawan = nama_karyawan;
    }

    public String getTanggal() {
        return tanggal;
    }
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getPengajuan_id() {
        return pengajuan_id;
    }
    public void setPengajuan_id(String pengajuan_id) {
        this.pengajuan_id = pengajuan_id;
    }

    public String getAls_team_employee_id() {
        return als_team_employee_id;
    }
    public void setAls_team_employee_id(String als_team_employee_id) {
        this.als_team_employee_id = als_team_employee_id;
    }

    public String getTanggal_absensi() {
        return tanggal_absensi;
    }
    public void setTanggal_absensi(String tanggal_absensi) {
        this.tanggal_absensi = tanggal_absensi;
    }

    public String getKeterangan() {
        return keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getStatus_pengajuan() {
        return status_pengajuan;
    }
    public void setStatus_pengajuan(String status_pengajuan) {
        this.status_pengajuan = status_pengajuan;
    }

    public String getAlasan_reject() {
        return alasan_reject;
    }
    public void setAlasan_reject(String alasan_reject) {
        this.alasan_reject = alasan_reject;
    }

    public String getStatus_notif() {
        return status_notif;
    }
    public void setStatus_notif(String status_notif) {
        this.status_notif = status_notif;
    }

}
