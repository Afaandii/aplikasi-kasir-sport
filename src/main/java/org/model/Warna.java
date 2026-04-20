package org.model;

import java.sql.Timestamp;

public class Warna {
    private int id;
    private String namaWarna;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Warna() {}

    public Warna(int id, String namaWarna) {
        this.id = id;
        this.namaWarna = namaWarna;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaWarna() {
        return namaWarna;
    }

    public void setNamaWarna(String namaWarna) {
        this.namaWarna = namaWarna;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
