
package carwash.view;

import carwash.dao.PaketDAO;
import carwash.model.PaketCuci;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PaketFrame extends JFrame implements ActionListener {

    private PaketDAO paketDAO = new PaketDAO();

    private JTable tabel;
    private DefaultTableModel modelTabel;

    private JButton btnRefresh;
    private JButton btnKembali;

    public PaketFrame() {

        setTitle("Data Paket Cuci - CarWash");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel judul = new JLabel(
            "DATA PAKET CUCI",
            SwingConstants.CENTER
        );

        judul.setFont(
            new Font("SansSerif", Font.BOLD, 24)
        );

        JLabel subjudul = new JLabel(
            "Daftar layanan CarWash dan Auto Detailing",
            SwingConstants.CENTER
        );

        JPanel panelJudul = new JPanel(
            new GridLayout(2, 1, 0, 5)
        );

        panelJudul.setBorder(
            BorderFactory.createEmptyBorder(20, 10, 10, 10)
        );

        panelJudul.add(judul);
        panelJudul.add(subjudul);

        add(panelJudul, BorderLayout.NORTH);

        modelTabel = new DefaultTableModel(
            new Object[]{
                "ID",
                "Kode Paket",
                "Nama Paket",
                "Tipe Paket",
                "Harga Dasar",
                "Durasi (Menit)",
                "Material"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(modelTabel);
        tabel.setRowHeight(28);
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(tabel);

        JPanel panelTabel = new JPanel(new BorderLayout());
        panelTabel.setBorder(
            BorderFactory.createTitledBorder("Daftar Paket")
        );

        panelTabel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15),
                BorderFactory.createTitledBorder("Daftar Paket")
            )
        );

        panelTabel.add(scrollPane, BorderLayout.CENTER);

        add(panelTabel, BorderLayout.CENTER);

        btnRefresh = new JButton("Refresh Data");
        btnKembali = new JButton("Kembali ke Menu Utama");

        btnRefresh.addActionListener(this);
        btnKembali.addActionListener(this);

        JPanel panelTombol = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        panelTombol.add(btnRefresh);
        panelTombol.add(btnKembali);

        add(panelTombol, BorderLayout.SOUTH);

        muatData();
    }

    private void muatData() {

        try {

            List<PaketCuci> daftar = paketDAO.semua();

            modelTabel.setRowCount(0);

            NumberFormat rupiah = NumberFormat.getCurrencyInstance(
                new Locale("id", "ID")
            );

            for (PaketCuci paket : daftar) {

                modelTabel.addRow(new Object[]{
                    paket.getId(),
                    paket.getKode(),
                    paket.getNama(),
                    paket.getTipe(),
                    rupiah.format(paket.getHargaDasar()),
                    paket.getDurasiDasar(),
                    paket.getMaterial()
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal memuat data paket: " + ex.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnRefresh) {

            muatData();

        } else if (e.getSource() == btnKembali) {

            new MenuUtama().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
            new PaketFrame().setVisible(true)
        );
    }
}
    
