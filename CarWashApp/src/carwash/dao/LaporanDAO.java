
package carwash.dao;

import carwash.config.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAO {

    // Mengambil transaksi selesai berdasarkan periode tanggal
    public List<Object[]> dataLaporan(LocalDate awal, LocalDate akhir)
            throws SQLException {

        String sql = "SELECT t.id_transaksi, t.no_antrean, p.nama, "
                + "k.plat_nomor, pc.nama_paket, t.waktu_selesai, "
                + "t.total_harga "
                + "FROM transaksi t "
                + "JOIN kendaraan k ON t.id_kendaraan = k.id_kendaraan "
                + "JOIN pelanggan p ON k.id_pelanggan = p.id_pelanggan "
                + "JOIN paket_cuci pc ON t.id_paket = pc.id_paket "
                + "WHERE t.status = 'SELESAI' "
                + "AND t.waktu_selesai >= ? "
                + "AND t.waktu_selesai < ? "
                + "ORDER BY t.waktu_selesai";

        List<Object[]> hasil = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(awal.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(akhir.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(new Object[]{
                        rs.getInt("id_transaksi"),
                        rs.getString("no_antrean"),
                        rs.getString("nama"),
                        rs.getString("plat_nomor"),
                        rs.getString("nama_paket"),
                        rs.getTimestamp("waktu_selesai"),
                        rs.getDouble("total_harga")
                    });
                }
            }
        }

        return hasil;
    }
    public double totalPendapatan(LocalDate awal, LocalDate akhir)
        throws SQLException {

    String sql = "SELECT COALESCE(SUM(total_harga), 0) "
               + "FROM transaksi "
               + "WHERE status = 'SELESAI' "
               + "AND waktu_selesai >= ? "
               + "AND waktu_selesai < ?";

    try (PreparedStatement ps = DatabaseConnection.getConnection()
            .prepareStatement(sql)) {

        ps.setTimestamp(1, Timestamp.valueOf(awal.atStartOfDay()));
        ps.setTimestamp(2, Timestamp.valueOf(akhir.plusDays(1).atStartOfDay()));

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
    }

    return 0;
}

    // Menghitung jumlah transaksi selesai pada periode tertentu
    public int jumlahTransaksi(LocalDate awal, LocalDate akhir)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM transaksi "
                + "WHERE status = 'SELESAI' "
                + "AND waktu_selesai >= ? "
                + "AND waktu_selesai < ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(awal.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(akhir.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
}
