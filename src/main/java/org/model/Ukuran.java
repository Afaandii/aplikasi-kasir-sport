package org.model;

import java.sql.Timestamp;

public class Ukuran {
    private int id;
    private String namaUkuran;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Ukuran() {}

    public Ukuran(int id, String namaUkuran) {
        this.id = id;
        this.namaUkuran = namaUkuran;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaUkuran() {
        return namaUkuran;
    }

    public void setNamaUkuran(String namaUkuran) {
        this.namaUkuran = namaUkuran;
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
