package carwash.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Mengelola satu koneksi JDBC bersama ke database MySQL.
 * DAO memakai koneksi ini tanpa menutupnya, jadi jangan dipakai di
 * try-with-resources. Cukup PreparedStatement dan ResultSet yang ditutup.
 */
public final class DatabaseConnection {
    // Sesuaikan URL, username, dan password database (default XAMPP)
    private static final String URL = "jdbc:mysql://localhost/carwash_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection koneksi;

    private DatabaseConnection() { }

    // Membuat koneksi baru jika belum ada atau sudah terputus
    public static synchronized Connection getConnection() throws SQLException {
        if (koneksi == null || koneksi.isClosed() || !koneksi.isValid(2)) {
            koneksi = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return koneksi;
    }

    // Dipanggil saat aplikasi ditutup
    public static synchronized void tutup() {
        if (koneksi != null) {
            try {
                koneksi.close();
            } catch (SQLException e) {
                System.out.println("Gagal menutup koneksi: " + e.getMessage());
            }
            koneksi = null;
        }
    }

    // Menguji koneksi tanpa membuka GUI
    public static void main(String[] args) {
        try {
            getConnection();
            System.out.println("Koneksi ke database berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        } finally {
            tutup();
        }
    }
}
