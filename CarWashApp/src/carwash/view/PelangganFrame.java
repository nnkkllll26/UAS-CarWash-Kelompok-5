
package carwash.view;

import carwash.dao.PelangganDAO;
import carwash.exception.DataTidakValidException;
import carwash.model.Pelanggan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PelangganFrame extends JFrame implements ActionListener {

    private final PelangganDAO pelangganDAO = new PelangganDAO();

    private JTextField txtNama, txtNoHp, txtCari;
    private JButton btnSimpan, btnUbah, btnHapus;
    private JButton btnBersih, btnCari, btnKembali;

    private JTable tabel;
    private DefaultTableModel modelTabel;

    private List<Pelanggan> semuaPelanggan = new ArrayList<>();
    private List<Pelanggan> pelangganTampil = new ArrayList<>();

    public PelangganFrame() {
        setTitle("Data Pelanggan - CarWash Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel judul = new JLabel(
            "DATA PELANGGAN", SwingConstants.CENTER
        );
        judul.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel subjudul = new JLabel(
            "Kelola informasi pelanggan CarWash",
            SwingConstants.CENTER
        );

        JPanel panelJudul = new JPanel(new GridLayout(2, 1, 0, 4));
        panelJudul.setBorder(
            BorderFactory.createEmptyBorder(16, 12, 8, 12)
        );

        panelJudul.add(judul);
        panelJudul.add(subjudul);

        add(panelJudul, BorderLayout.NORTH);
        add(buatPanelKonten(), BorderLayout.CENTER);
        add(buatPanelBawah(), BorderLayout.SOUTH);

        muatData();
    }

    private JPanel buatPanelKonten() {

        JPanel konten = new JPanel(new BorderLayout(10, 10));
        konten.setBorder(
            BorderFactory.createEmptyBorder(0, 18, 8, 18)
        );

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(
            BorderFactory.createTitledBorder("Form Pelanggan")
        );

        txtNama = new JTextField(25);
        txtNoHp = new JTextField(25);

        tambahBaris(form, 0, "Nama Pelanggan", txtNama);
        tambahBaris(form, 1, "Nomor HP", txtNoHp);

        JPanel tombol = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 6)
        );

        btnSimpan = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnBersih = new JButton("Bersihkan");

        for (JButton b : new JButton[]{
            btnSimpan, btnUbah, btnHapus, btnBersih
        }) {
            b.addActionListener(this);
            tombol.add(b);
        }

        JPanel atas = new JPanel(new BorderLayout(4, 4));
        atas.add(form, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        txtCari = new JTextField(24);
        btnCari = new JButton("Cari");

        btnCari.addActionListener(this);
        txtCari.addActionListener(this);

        JPanel pencarian = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        pencarian.add(new JLabel("Cari nama / nomor HP:"));
        pencarian.add(txtCari);
        pencarian.add(btnCari);

        modelTabel = new DefaultTableModel(
            new Object[]{"ID", "Nama Pelanggan", "Nomor HP"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(modelTabel);
        tabel.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        tabel.setRowHeight(25);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBaris();
            }
        });

        JPanel daftar = new JPanel(new BorderLayout(0, 6));
        daftar.setBorder(
            BorderFactory.createTitledBorder("Daftar Pelanggan")
        );

        daftar.add(pencarian, BorderLayout.NORTH);
        daftar.add(new JScrollPane(tabel), BorderLayout.CENTER);

        konten.add(atas, BorderLayout.NORTH);
        konten.add(daftar, BorderLayout.CENTER);

        return konten;
    }

    private JPanel buatPanelBawah() {

        btnKembali = new JButton("Kembali ke Menu Utama");
        btnKembali.addActionListener(this);

        JPanel bawah = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        bawah.add(btnKembali);

        return bawah;
    }

    private void tambahBaris(
        JPanel panel, int baris,
        String label, Component isian
    ) {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridy = baris;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(isian, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object sumber = e.getSource();

        if (sumber == btnSimpan) {
            simpan();

        } else if (sumber == btnUbah) {
            ubah();

        } else if (sumber == btnHapus) {
            hapus();

        } else if (sumber == btnBersih) {
            bersihkanForm();

        } else if (sumber == btnCari || sumber == txtCari) {
            terapkanFilter();

        } else if (sumber == btnKembali) {
            new MenuUtama().setVisible(true);
            dispose();
        }
    }

    private void simpan() {

        try {
            Pelanggan pelanggan = new Pelanggan(
                0,
                txtNama.getText(),
                txtNoHp.getText()
            );

            pelangganDAO.tambah(pelanggan);

            info("Data pelanggan berhasil ditambahkan.");

            bersihkanForm();
            muatData();

        } catch (DataTidakValidException ex) {
            peringatan(ex.getMessage());

        } catch (SQLException ex) {
            tanganiError(ex, "menambahkan data pelanggan");
        }
    }

    private void ubah() {

        Pelanggan terpilih = pelangganTerpilih();

        if (terpilih == null) {
            peringatan(
                "Pilih data pelanggan pada tabel terlebih dahulu."
            );
            return;
        }

        try {
            Pelanggan pelanggan = new Pelanggan(
                terpilih.getId(),
                txtNama.getText(),
                txtNoHp.getText()
            );

            pelangganDAO.ubah(pelanggan);

            info("Data pelanggan berhasil diubah.");

            bersihkanForm();
            muatData();

        } catch (DataTidakValidException ex) {
            peringatan(ex.getMessage());

        } catch (SQLException ex) {
            tanganiError(ex, "mengubah data pelanggan");
        }
    }

    private void hapus() {

        Pelanggan terpilih = pelangganTerpilih();

        if (terpilih == null) {
            peringatan(
                "Pilih data pelanggan pada tabel terlebih dahulu."
            );
            return;
        }

        int pilihan = JOptionPane.showConfirmDialog(
            this,
            "Hapus data pelanggan \"" +
            terpilih.getNama() + "\"?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            pelangganDAO.hapus(terpilih.getId());

            info("Data pelanggan berhasil dihapus.");

            bersihkanForm();
            muatData();

        } catch (SQLException ex) {
            tanganiError(ex, "menghapus data pelanggan");
        }
    }

    private Pelanggan pelangganTerpilih() {

        int baris = tabel.getSelectedRow();

        if (baris < 0 || baris >= pelangganTampil.size()) {
            return null;
        }

        return pelangganTampil.get(baris);
    }

    private void isiFormDariBaris() {

        Pelanggan p = pelangganTerpilih();

        if (p != null) {
            txtNama.setText(p.getNama());
            txtNoHp.setText(p.getNoHp());
        }
    }

    private void muatData() {

        try {
            semuaPelanggan = pelangganDAO.semua();
            terapkanFilter();

        } catch (SQLException ex) {
            tanganiError(ex, "memuat data pelanggan");
        }
    }

    private void terapkanFilter() {

        String kataKunci = txtCari.getText()
            .trim().toLowerCase();

        pelangganTampil = new ArrayList<>();

        for (Pelanggan p : semuaPelanggan) {

            if (kataKunci.isEmpty()
                || p.getNama().toLowerCase().contains(kataKunci)
                || p.getNoHp().toLowerCase().contains(kataKunci)) {

                pelangganTampil.add(p);
            }
        }

        modelTabel.setRowCount(0);

        for (Pelanggan p : pelangganTampil) {

            modelTabel.addRow(new Object[]{
                p.getId(),
                p.getNama(),
                p.getNoHp()
            });
        }
    }

    private void bersihkanForm() {

        txtNama.setText("");
        txtNoHp.setText("");

        tabel.clearSelection();
        txtNama.requestFocus();
    }

    private void tanganiError(SQLException ex, String aksi) {

        String pesan;

        if (ex.getErrorCode() == 1451) {
            pesan = "Pelanggan tidak dapat dihapus karena masih memiliki data kendaraan.";
        } else {
            pesan = "Gagal " + aksi + ": " + ex.getMessage();
        }

        JOptionPane.showMessageDialog(
            this,
            pesan,
            "Kesalahan",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void info(String pesan) {

        JOptionPane.showMessageDialog(
            this,
            pesan,
            "Informasi",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void peringatan(String pesan) {

        JOptionPane.showMessageDialog(
            this,
            pesan,
            "Peringatan",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
            new PelangganFrame().setVisible(true)
        );
    }
}
