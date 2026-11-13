package dao;

import config.DatabaseConnection;
import exception.DataTidakValidException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CuciHydraulic;
import model.CuciReguler;
import model.NanoCoating;
import model.PaketCuci;

public class PaketDAO {

    /** Baris tabel dipetakan ke subclass yang sesuai (inheritance + polymorphism). */
    public List<PaketCuci> semua() throws SQLException {
        String sql = "SELECT id_paket, kode_paket, nama_paket, tipe_paket, harga_dasar, durasi_dasar, material "
                + "FROM paket_cuci ORDER BY id_paket";
        List<PaketCuci> hasil = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_paket");
                String kode = rs.getString("kode_paket");
                String nama = rs.getString("nama_paket");
                double harga = rs.getDouble("harga_dasar");
                int durasi = rs.getInt("durasi_dasar");
                String material = rs.getString("material");
                try {
                    switch (rs.getString("tipe_paket")) {
                        case "HYDRAULIC":
                            hasil.add(new CuciHydraulic(id, kode, nama, harga, durasi, material));
                            break;
                        case "NANO":
                            hasil.add(new NanoCoating(id, kode, nama, harga, durasi, material));
                            break;
                        default:
                            hasil.add(new CuciReguler(id, kode, nama, harga, durasi, material));
                    }
                } catch (DataTidakValidException e) {
                    throw new SQLException("Data paket di database tidak valid: " + e.getMessage(), e);
                }
            }
        }
        return hasil;
    }
}
