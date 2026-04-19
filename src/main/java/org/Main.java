package org;

import org.utils.Database;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Memulai aplikasi POS Toko Sport...");
        
        // Test koneksi ke database
        Connection conn = Database.getConnection();
        
        if (conn != null) {
            System.out.println("Siap untuk memproses data.");
            Database.closeConnection();
        } else {
            System.out.println("Pastikan MySQL sudah berjalan dan database 'db_penjualan_toko_sport' sudah dibuat.");
        }
    }
}