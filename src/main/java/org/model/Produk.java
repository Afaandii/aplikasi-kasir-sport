package org.model;

import java.sql.Timestamp;

public class Produk {
    private int id;
    private int kategoriId;
    private int merekId;
    private String kodeProduk;
    private String namaProduk;
    private int hargaPokok;
    private int hargaJual;
    private String thumbnail;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Display fields
    private String kategoriNama;
    private String merekNama;

    public Produk() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKategoriId() { return kategoriId; }
    public void setKategoriId(int kategoriId) { this.kategoriId = kategoriId; }

    public int getMerekId() { return merekId; }
    public void setMerekId(int merekId) { this.merekId = merekId; }

    public String getKodeProduk() { return kodeProduk; }
    public void setKodeProduk(String kodeProduk) { this.kodeProduk = kodeProduk; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public int getHargaPokok() { return hargaPokok; }
    public void setHargaPokok(int hargaPokok) { this.hargaPokok = hargaPokok; }

    public int getHargaJual() { return hargaJual; }
    public void setHargaJual(int hargaJual) { this.hargaJual = hargaJual; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getKategoriNama() { return kategoriNama; }
    public void setKategoriNama(String kategoriNama) { this.kategoriNama = kategoriNama; }

    public String getMerekNama() { return merekNama; }
    public void setMerekNama(String merekNama) { this.merekNama = merekNama; }
}
