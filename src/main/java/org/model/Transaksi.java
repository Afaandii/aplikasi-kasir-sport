package org.model;

import java.sql.Timestamp;

public class Transaksi {
    private int id;
    private int userId;
    private String kodeTransaksi;
    private String namaCustomer;
    private int totalPembayaran;
    private int uangMasuk;
    private int kembalian;
    private String metodePembayaran;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Display fields
    private String kasirNama;

    public Transaksi() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getKodeTransaksi() { return kodeTransaksi; }
    public void setKodeTransaksi(String kodeTransaksi) { this.kodeTransaksi = kodeTransaksi; }

    public String getNamaCustomer() { return namaCustomer; }
    public void setNamaCustomer(String namaCustomer) { this.namaCustomer = namaCustomer; }

    public int getTotalPembayaran() { return totalPembayaran; }
    public void setTotalPembayaran(int totalPembayaran) { this.totalPembayaran = totalPembayaran; }

    public int getUangMasuk() { return uangMasuk; }
    public void setUangMasuk(int uangMasuk) { this.uangMasuk = uangMasuk; }

    public int getKembalian() { return kembalian; }
    public void setKembalian(int kembalian) { this.kembalian = kembalian; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getKasirNama() { return kasirNama; }
    public void setKasirNama(String kasirNama) { this.kasirNama = kasirNama; }
}
