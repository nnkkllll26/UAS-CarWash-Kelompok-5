package carwash.view;

import carwash.config.DatabaseConnection;
import carwash.dao.KendaraanDAO;
import carwash.dao.PelangganDAO;
import carwash.exception.DataTidakValidException;
import carwash.model.JenisKendaraan;
import carwash.model.Kendaraan;
import carwash.model.Pelanggan;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Frame pengelolaan data kendaraan (tambah, ubah, hapus, cari).
 * Validasi input dilakukan oleh kelas model Kendaraan lewat
 * DataTidakValidException. Memakai ActionListener untuk tombol dan
 * ListSelectionListener untuk pemilihan baris tabel.
 */
public class KendaraanFrame extends JFrame implements ActionListener {
    private final KendaraanDAO kendaraanDAO = new KendaraanDAO();
    private final PelangganDAO pelangganDAO = new PelangganDAO();

    private List<Pelanggan> daftarPelanggan = new ArrayList<>();
    private List<Kendaraan> semuaKendaraan = new ArrayList<>();
    private List<Kendaraan> tampil = new ArrayList<>(); // baris yang sedang ada di tabel

    private JComboBox<String> cmbPelanggan;
    private JComboBox<JenisKendaraan> cmbJenis;
    private JTextField txtPlat, txtMerk, txtCari;
    private JButton btnSimpan, btnUbah, btnHapus, btnBersih, btnCari, btnKembali;
    private JTable tabel;
    private DefaultTableModel modelTabel;

    public KendaraanFrame() {
        setTitle("Data Kendaraan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Menutup koneksi database saat jendela ditutup lewat tombol X
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.tutup();
            }
        });

        add(buatPanelAtas(), BorderLayout.NORTH);
        add(buatPanelTengah(), BorderLayout.CENTER);
        add(buatPanelBawah(), BorderLayout.SOUTH);

        muatData();
    }

    // ---------------------- Pembentukan tampilan ----------------------

    private JPanel buatPanelAtas() {
        cmbPelanggan = new JComboBox<>();
        cmbJenis = new JComboBox<>(JenisKendaraan.values());
        txtPlat = new JTextField(20);
        txtMerk = new JTextField(20);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Form Kendaraan"));
        tambahBaris(form, 0, "Pemilik", cmbPelanggan);
        tambahBaris(form, 1, "Plat Nomor", txtPlat);
        tambahBaris(form, 2, "Jenis Kendaraan", cmbJenis);
        tambahBaris(form, 3, "Merk", txtMerk);

        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnBersih = new JButton("Bersihkan");
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        for (JButton tombol : new JButton[] {btnSimpan, btnUbah, btnHapus, btnBersih}) {
            tombol.addActionListener(this);
            panelTombol.add(tombol);
        }

        JPanel atas = new JPanel(new BorderLayout());
        atas.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        atas.add(form, BorderLayout.CENTER);
        atas.add(panelTombol, BorderLayout.SOUTH);
        return atas;
    }

    private JPanel buatPanelTengah() {
        txtCari = new JTextField(25);
        txtCari.addActionListener(this); // tekan Enter untuk mencari
        btnCari = new JButton("Cari");
        btnCari.addActionListener(this);

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelCari.add(new JLabel("Cari (plat, merk, atau nama pemilik):"));
        panelCari.add(txtCari);
        panelCari.add(btnCari);

        String[] kolom = {"ID", "Pemilik", "Plat Nomor", "Jenis", "Merk"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int baris, int kolomKe) {
                return false; // data diubah lewat form, bukan langsung di tabel
            }
        };
        tabel = new JTable(modelTabel);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getColumnModel().getColumn(0).setMaxWidth(50);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBaris();
            }
        });

        JPanel tengah = new JPanel(new BorderLayout(0, 8));
        tengah.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        tengah.add(panelCari, BorderLayout.NORTH);
        tengah.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return tengah;
    }

    private JPanel buatPanelBawah() {
        btnKembali = new JButton("Kembali ke Menu");
        btnKembali.addActionListener(this);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bawah.add(btnKembali);
        return bawah;
    }

    private void tambahBaris(JPanel panel, int baris, String label, Component isian) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.gridy = baris;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(isian, gbc);
    }

    // ---------------------------- Event ----------------------------

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
            Kendaraan baru = new Kendaraan(0, txtPlat.getText(), txtMerk.getText(),
                    (JenisKendaraan) cmbJenis.getSelectedItem(), pemilikTerpilih());
            kendaraanDAO.tambah(baru);
            info("Data kendaraan berhasil disimpan.");
            bersihkanForm();
            muatData();
        } catch (DataTidakValidException ex) {
            peringatan(ex.getMessage());
        } catch (SQLException ex) {
            tanganiError(ex, "menyimpan data");
        }
    }

    private void ubah() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih data pada tabel terlebih dahulu.");
            return;
        }
        try {
            Kendaraan diubah = new Kendaraan(tampil.get(baris).getId(), txtPlat.getText(),
                    txtMerk.getText(), (JenisKendaraan) cmbJenis.getSelectedItem(), pemilikTerpilih());
            kendaraanDAO.ubah(diubah);
            info("Data kendaraan berhasil diubah.");
            bersihkanForm();
            muatData();
        } catch (DataTidakValidException ex) {
            peringatan(ex.getMessage());
        } catch (SQLException ex) {
            tanganiError(ex, "mengubah data");
        }
    }

    private void hapus() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih data pada tabel terlebih dahulu.");
            return;
        }
        Kendaraan terpilih = tampil.get(baris);
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Hapus kendaraan " + terpilih.getPlatNomor() + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            kendaraanDAO.hapus(terpilih.getId());
            info("Data kendaraan berhasil dihapus.");
            bersihkanForm();
            muatData();
        } catch (SQLException ex) {
            tanganiError(ex, "menghapus data");
        }
    }

    // Mengisi form saat baris tabel dipilih
    private void isiFormDariBaris() {
        int baris = tabel.getSelectedRow();
        if (baris < 0 || baris >= tampil.size()) {
            return;
        }
        Kendaraan k = tampil.get(baris);
        for (int i = 0; i < daftarPelanggan.size(); i++) {
            if (daftarPelanggan.get(i).getId() == k.getPemilik().getId()) {
                cmbPelanggan.setSelectedIndex(i);
                break;
            }
        }
        txtPlat.setText(k.getPlatNomor());
        cmbJenis.setSelectedItem(k.getJenis());
        txtMerk.setText(k.getMerk());
    }

    // ------------------------ Data dan tampilan ------------------------

    // Memuat ulang pelanggan dan kendaraan dari database
    private void muatData() {
        try {
            daftarPelanggan = pelangganDAO.semua();
            Map<Integer, Pelanggan> pelangganById = new LinkedHashMap<>();
            cmbPelanggan.removeAllItems();
            for (Pelanggan p : daftarPelanggan) {
                pelangganById.put(p.getId(), p);
                cmbPelanggan.addItem(p.getNama() + " (" + p.getNoHp() + ")");
            }
            semuaKendaraan = kendaraanDAO.semua(pelangganById);
            txtCari.setText("");
            terapkanFilter();
        } catch (SQLException ex) {
            tanganiError(ex, "memuat data");
        }
    }

    // Menyaring daftar kendaraan berdasarkan kata kunci pencarian
    private void terapkanFilter() {
        String kunci = txtCari.getText().trim().toLowerCase();
        tampil = new ArrayList<>();
        for (Kendaraan k : semuaKendaraan) {
            boolean cocok = kunci.isEmpty()
                    || k.getPlatNomor().toLowerCase().contains(kunci)
                    || k.getMerk().toLowerCase().contains(kunci)
                    || k.getPemilik().getNama().toLowerCase().contains(kunci);
            if (cocok) {
                tampil.add(k);
            }
        }
        modelTabel.setRowCount(0);
        for (Kendaraan k : tampil) {
            modelTabel.addRow(new Object[] {
                k.getId(), k.getPemilik().getNama(), k.getPlatNomor(), k.getJenis(), k.getMerk()
            });
        }
    }

    // Mengambil pemilik dari pilihan combo, melempar pesan jika belum ada
    private Pelanggan pemilikTerpilih() throws DataTidakValidException {
        int indeks = cmbPelanggan.getSelectedIndex();
        if (indeks < 0) {
            throw new DataTidakValidException("Belum ada data pelanggan. Tambahkan pelanggan terlebih dahulu.");
        }
        return daftarPelanggan.get(indeks);
    }

    private void bersihkanForm() {
        txtPlat.setText("");
        txtMerk.setText("");
        cmbJenis.setSelectedIndex(0);
        if (cmbPelanggan.getItemCount() > 0) {
            cmbPelanggan.setSelectedIndex(0);
        }
        tabel.clearSelection();
    }

    // ------------------------ Pesan ke pengguna ------------------------

    private void tanganiError(SQLException ex, String aksi) {
        String pesan;
        switch (ex.getErrorCode()) {
            case 1062:
                pesan = "Plat nomor sudah terdaftar.";
                break;
            case 1451:
                pesan = "Kendaraan tidak dapat dihapus karena sudah dipakai pada transaksi.";
                break;
            case 1452:
                pesan = "Pemilik yang dipilih tidak valid.";
                break;
            default:
                pesan = "Gagal " + aksi + ": " + ex.getMessage();
        }
        JOptionPane.showMessageDialog(this, pesan, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    private void info(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    // Untuk menguji frame ini secara terpisah
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KendaraanFrame().setVisible(true);
            }
        });
    }
}
