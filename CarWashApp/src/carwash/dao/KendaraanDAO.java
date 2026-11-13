package dao;

import config.DatabaseConnection;
import exception.DataTidakValidException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.JenisKendaraan;
import model.Kendaraan;
import model.Pelanggan;

public class KendaraanDAO {

    public List<Kendaraan> semua(Map<Integer, Pelanggan> pelangganById) throws SQLException {
        String sql = "SELECT id_kendaraan, plat_nomor, merk, jenis, id_pelanggan FROM kendaraan ORDER BY plat_nomor";
        List<Kendaraan> hasil = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    hasil.add(new Kendaraan(rs.getInt("id_kendaraan"), rs.getString("plat_nomor"),
                            rs.getString("merk"), JenisKendaraan.valueOf(rs.getString("jenis")),
                            pelangganById.get(rs.getInt("id_pelanggan"))));
                } catch (DataTidakValidException e) {
                    throw new SQLException("Data kendaraan di database tidak valid: " + e.getMessage(), e);
                }
            }
        }
        return hasil;
    }

    public void tambah(Kendaraan k) throws SQLException {
        String sql = "INSERT INTO kendaraan (plat_nomor, merk, jenis, id_pelanggan) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.getPlatNomor());
            ps.setString(2, k.getMerk());
            ps.setString(3, k.getJenis().name());
            ps.setInt(4, k.getPemilik().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    k.setId(keys.getInt(1));
                }
            }
        }
    }

    public void ubah(Kendaraan k) throws SQLException {
        String sql = "UPDATE kendaraan SET plat_nomor = ?, merk = ?, jenis = ?, id_pelanggan = ? WHERE id_kendaraan = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getPlatNomor());
            ps.setString(2, k.getMerk());
            ps.setString(3, k.getJenis().name());
            ps.setInt(4, k.getPemilik().getId());
            ps.setInt(5, k.getId());
            ps.executeUpdate();
        }
    }

    public void hapus(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM kendaraan WHERE id_kendaraan = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
