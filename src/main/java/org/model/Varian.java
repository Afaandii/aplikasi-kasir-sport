package org.model;

import java.sql.Timestamp;

public class Varian {
    private int id;
    private int produkId;
    private int ukuranId;
    private int warnaId;
    private int stokProduk;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Display fields
    private String ukuranNama;
    private String warnaNama;

    public Varian() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }

    public int getUkuranId() { return ukuranId; }
    public void setUkuranId(int ukuranId) { this.ukuranId = ukuranId; }

    public int getWarnaId() { return warnaId; }
    public void setWarnaId(int warnaId) { this.warnaId = warnaId; }

    public int getStokProduk() { return stokProduk; }
    public void setStokProduk(int stokProduk) { this.stokProduk = stokProduk; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getUkuranNama() { return ukuranNama; }
    public void setUkuranNama(String ukuranNama) { this.ukuranNama = ukuranNama; }

    public String getWarnaNama() { return warnaNama; }
    public void setWarnaNama(String warnaNama) { this.warnaNama = warnaNama; }
}
