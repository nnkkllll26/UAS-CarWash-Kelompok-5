package dao;

import config.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Kendaraan;
import model.PaketCuci;
import model.StatusAntrean;
import model.Transaksi;
import model.Washer;

public class TransaksiDAO {

    /** SELECT: membaca semua transaksi lalu merakit objeknya memakai Map hasil load tabel lain. */
    public List<Transaksi> semua(Map<Integer, Kendaraan> kendaraanById, Map<Integer, PaketCuci> paketById,
                                 Map<Integer, Washer> washerById) throws SQLException {
        String sql = "SELECT id_transaksi, no_antrean, id_kendaraan, id_paket, id_washer, status, "
                + "waktu_masuk, estimasi_selesai, waktu_selesai, total_harga FROM transaksi ORDER BY id_transaksi";
        List<Transaksi> hasil = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idWasher = rs.getInt("id_washer");
                Washer washer = rs.wasNull() ? null : washerById.get(idWasher);
                hasil.add(new Transaksi(rs.getInt("id_transaksi"), rs.getString("no_antrean"),
                        kendaraanById.get(rs.getInt("id_kendaraan")), paketById.get(rs.getInt("id_paket")),
                        washer, StatusAntrean.valueOf(rs.getString("status")),
                        ambil(rs.getTimestamp("waktu_masuk")), ambil(rs.getTimestamp("estimasi_selesai")),
                        ambil(rs.getTimestamp("waktu_selesai")), rs.getDouble("total_harga")));
            }
        }
        return hasil;
    }

    /** INSERT: menyimpan antrean baru memakai PreparedStatement. */
    public void tambah(Transaksi t) throws SQLException {
        String sql = "INSERT INTO transaksi (no_antrean, id_kendaraan, id_paket, id_washer, status, "
                + "waktu_masuk, estimasi_selesai, waktu_selesai, total_harga) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            isiParameter(ps, t, true);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getInt(1));
                }
            }
        }
    }

    /** UPDATE: memperbarui status, washer, dan estimasi. */
    public void perbarui(Transaksi t) throws SQLException {
        String sql = "UPDATE transaksi SET id_washer = ?, status = ?, estimasi_selesai = ?, waktu_selesai = ? "
                + "WHERE id_transaksi = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            if (t.getWasher() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, t.getWasher().getId());
            }
            ps.setString(2, t.getStatus().name());
            ps.setTimestamp(3, simpan(t.getEstimasiSelesai()));
            ps.setTimestamp(4, simpan(t.getWaktuSelesai()));
            ps.setInt(5, t.getId());
            ps.executeUpdate();
        }
    }

    /** DELETE: menghapus satu transaksi berdasarkan id. */
    public void hapus(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM transaksi WHERE id_transaksi = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void isiParameter(PreparedStatement ps, Transaksi t, boolean baru) throws SQLException {
        ps.setString(1, t.getNoAntrean());
        ps.setInt(2, t.getKendaraan().getId());
        ps.setInt(3, t.getPaket().getId());
        if (t.getWasher() == null) {
            ps.setNull(4, java.sql.Types.INTEGER);
        } else {
            ps.setInt(4, t.getWasher().getId());
        }
        ps.setString(5, t.getStatus().name());
        ps.setTimestamp(6, simpan(t.getWaktuMasuk()));
        ps.setTimestamp(7, simpan(t.getEstimasiSelesai()));
        ps.setTimestamp(8, simpan(t.getWaktuSelesai()));
        ps.setDouble(9, t.getTotalHarga());
    }

    private LocalDateTime ambil(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    private Timestamp simpan(LocalDateTime waktu) {
        return waktu == null ? null : Timestamp.valueOf(waktu);
    }
}
