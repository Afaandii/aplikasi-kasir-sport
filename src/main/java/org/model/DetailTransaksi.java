package org.model;

public class DetailTransaksi {
    private int id;
    private int transaksiId;
    private int varianId;
    private int jumlah;
    private int hargaSatuan;
    private int subtotal;
    
    // Display fields
    private String namaProduk;
    private String ukuranNama;
    private String warnaNama;

    public DetailTransaksi() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaksiId() { return transaksiId; }
    public void setTransaksiId(int transaksiId) { this.transaksiId = transaksiId; }

    public int getVarianId() { return varianId; }
    public void setVarianId(int varianId) { this.varianId = varianId; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public int getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(int hargaSatuan) { this.hargaSatuan = hargaSatuan; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public String getUkuranNama() { return ukuranNama; }
    public void setUkuranNama(String ukuranNama) { this.ukuranNama = ukuranNama; }

    public String getWarnaNama() { return warnaNama; }
    public void setWarnaNama(String warnaNama) { this.warnaNama = warnaNama; }
}
