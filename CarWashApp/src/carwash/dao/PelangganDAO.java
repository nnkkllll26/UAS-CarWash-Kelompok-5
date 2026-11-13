package dao;

import config.DatabaseConnection;
import exception.DataTidakValidException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Pelanggan;

public class PelangganDAO {

    public List<Pelanggan> semua() throws SQLException {
        String sql = "SELECT id_pelanggan, nama, no_hp FROM pelanggan ORDER BY nama";
        List<Pelanggan> hasil = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    hasil.add(new Pelanggan(rs.getInt("id_pelanggan"), rs.getString("nama"), rs.getString("no_hp")));
                } catch (DataTidakValidException e) {
                    throw new SQLException("Data pelanggan di database tidak valid: " + e.getMessage(), e);
                }
            }
        }
        return hasil;
    }

    public void tambah(Pelanggan p) throws SQLException {
        String sql = "INSERT INTO pelanggan (nama, no_hp) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                }
            }
        }
    }

    public void ubah(Pelanggan p) throws SQLException {
        String sql = "UPDATE pelanggan SET nama = ?, no_hp = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
        }
    }

    public void hapus(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM pelanggan WHERE id_pelanggan = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
