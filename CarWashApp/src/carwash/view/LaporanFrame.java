
package carwash.view;

import carwash.dao.LaporanDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.print.PrinterException;

public class LaporanFrame extends JFrame {

    private final LaporanDAO laporanDAO = new LaporanDAO();

    private final JTextField tanggalAwal =
            new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 10);

    private final JTextField tanggalAkhir =
            new JTextField(LocalDate.now().toString(), 10);

    private final JLabel labelTotal =
            new JLabel("Total Pendapatan: Rp 0");

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new String[]{"ID", "No. Antrean", "Pelanggan",
                        "Plat Nomor", "Paket Cuci", "Waktu Selesai", "Total Harga"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable tabel = new JTable(model);

    public LaporanFrame() {
        setTitle("Laporan Pendapatan CarWash");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));

        JLabel judul = new JLabel(
                "LAPORAN PENDAPATAN CARWASH",
                SwingConstants.CENTER);
        judul.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));

        JPanel panelAtas = new JPanel(new FlowLayout());

        panelAtas.add(new JLabel("Tanggal Awal:"));
        panelAtas.add(tanggalAwal);
        panelAtas.add(new JLabel("Tanggal Akhir:"));
        panelAtas.add(tanggalAkhir);

        JButton tombolTampilkan = new JButton("Tampilkan");
        JButton tombolRefresh = new JButton("Refresh");

        panelAtas.add(tombolTampilkan);
        panelAtas.add(tombolRefresh);

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.add(judul, BorderLayout.NORTH);
        panelHeader.add(panelAtas, BorderLayout.CENTER);

        panelUtama.add(panelHeader, BorderLayout.NORTH);
        panelUtama.add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        labelTotal.setFont(new java.awt.Font(
                "Arial", java.awt.Font.BOLD, 16));

        JButton tombolCetak = new JButton("Cetak");
        JButton tombolPDF = new JButton("Ekspor PDF");
        JButton tombolKembali = new JButton("Kembali");

        panelBawah.add(labelTotal);
        panelBawah.add(tombolCetak);
        panelBawah.add(tombolPDF);
        panelBawah.add(tombolKembali);

        panelUtama.add(panelBawah, BorderLayout.SOUTH);

        add(panelUtama);

        tombolTampilkan.addActionListener(e -> tampilkanLaporan());
        tombolRefresh.addActionListener(e -> tampilkanLaporan());
        tombolCetak.addActionListener(e -> {
            try {
                boolean berhasil = tabel.print();

                if (berhasil) {
                    JOptionPane.showMessageDialog(this,
                            "Laporan berhasil dicetak.");
                }

            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                        "Gagal mencetak laporan: " + ex.getMessage(),
                        "Kesalahan",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        tombolKembali.addActionListener(e -> {
            new MenuUtama().setVisible(true);
            dispose();
        });
        tampilkanLaporan();
    }

    private void tampilkanLaporan() {
        try {
            LocalDate awal = LocalDate.parse(tanggalAwal.getText());
            LocalDate akhir = LocalDate.parse(tanggalAkhir.getText());

            if (akhir.isBefore(awal)) {
                JOptionPane.showMessageDialog(this,
                        "Tanggal akhir tidak boleh sebelum tanggal awal.");
                return;
            }

            model.setRowCount(0);

            List<Object[]> data =
                    laporanDAO.dataLaporan(awal, akhir);

            for (Object[] baris : data) {
                model.addRow(new Object[]{
                    baris[0],
                    baris[1],
                    baris[2],
                    baris[3],
                    baris[4],
                    baris[5],
                    "Rp " + String.format("%,.0f", (Double) baris[6])
                });
            }

            double total =
                    laporanDAO.totalPendapatan(awal, akhir);

            labelTotal.setText(
                    "Total Pendapatan: Rp "
                    + String.format("%,.0f", total));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat laporan: " + e.getMessage(),
                    "Kesalahan",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
