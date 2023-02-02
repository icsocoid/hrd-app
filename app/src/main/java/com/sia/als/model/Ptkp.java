package com.sia.als.model;

public class Ptkp {
    private String id;
    private String kodePtkp;
    private String namaPtkp;
    private String nominal;
    private String descriptions;

    public Ptkp()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKodePtkp() {
        return kodePtkp;
    }

    public void setKodePtkp(String kodePtkp) {
        this.kodePtkp = kodePtkp;
    }

    public String getNamaPtkp() {
        return namaPtkp;
    }

    public void setNamaPtkp(String namaPtkp) {
        this.namaPtkp = namaPtkp;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
}
