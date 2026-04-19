package org.model;

import java.sql.Timestamp;

public class Merek {
    private int id;
    private String namaMerek;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Merek() {}

    public Merek(int id, String namaMerek) {
        this.id = id;
        this.namaMerek = namaMerek;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaMerek() {
        return namaMerek;
    }

    public void setNamaMerek(String namaMerek) {
        this.namaMerek = namaMerek;
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
