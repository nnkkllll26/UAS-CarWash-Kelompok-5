package dao;

import config.DatabaseConnection;
import exception.DataTidakValidException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Spesialisasi;
import model.StatusWasher;
import model.Washer;

public class WasherDAO {

    public List<Washer> semua() throws SQLException {
        String sql = "SELECT id_washer, nama, no_hp, spesialisasi, status FROM washer ORDER BY id_washer";
        List<Washer> hasil = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    hasil.add(new Washer(rs.getInt("id_washer"), rs.getString("nama"), rs.getString("no_hp"),
                            Spesialisasi.valueOf(rs.getString("spesialisasi")),
                            StatusWasher.valueOf(rs.getString("status"))));
                } catch (DataTidakValidException e) {
                    throw new SQLException("Data washer di database tidak valid: " + e.getMessage(), e);
                }
            }
        }
        return hasil;
    }

    public void ubahStatus(int idWasher, StatusWasher status) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE washer SET status = ? WHERE id_washer = ?")) {
            ps.setString(1, status.name());
            ps.setInt(2, idWasher);
            ps.executeUpdate();
        }
    }
}
