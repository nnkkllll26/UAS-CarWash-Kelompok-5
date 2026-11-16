package carwash.view;

import carwash.dao.TransaksiDAO;
import carwash.dao.KendaraanDAO;
import carwash.dao.PaketDAO;
import carwash.dao.PelangganDAO;
import carwash.dao.WasherDAO;

import carwash.model.Transaksi;
import carwash.model.Kendaraan;
import carwash.model.PaketCuci;
import carwash.model.Pelanggan;
import carwash.model.Washer;
import carwash.model.StatusAntrean;
import carwash.model.StatusWasher;

import carwash.exception.DataTidakValidException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransaksiFrame extends JFrame {

    private TransaksiDAO transaksiDAO = new TransaksiDAO();
    private KendaraanDAO kendaraanDAO = new KendaraanDAO();
    private PaketDAO paketDAO = new PaketDAO();
    private PelangganDAO pelangganDAO = new PelangganDAO();
    private WasherDAO washerDAO = new WasherDAO();

    private JTable tabel;
    private DefaultTableModel modelTabel;

    private JComboBox<Kendaraan> cmbKendaraan;
    private JComboBox<PaketCuci> cmbPaket;
    private JComboBox<Washer> cmbWasher;

    private JButton btnTambah;
    private JButton btnProses;
    private JButton btnSelesai;
    private JButton btnBatal;
    private JButton btnStruk;
    private JButton btnKembali;
    private JButton btnRefresh;

    private java.util.List<Transaksi> daftarTransaksi = new ArrayList<>();
    private java.util.List<Washer> daftarWasher = new ArrayList<>();

    private Map<Integer, Kendaraan> kendaraanById = new HashMap<>();
    private Map<Integer, PaketCuci> paketById = new HashMap<>();
    private Map<Integer, Washer> washerById = new HashMap<>();

    private DateTimeFormatter formatWaktu =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public TransaksiFrame() {

        setTitle("Antrean dan Transaksi CarWash");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // Judul
        JLabel judul = new JLabel(
                "Antrean dan Transaksi CarWash",
                SwingConstants.CENTER
        );

        judul.setFont(new Font("SansSerif", Font.BOLD, 22));
        judul.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        add(judul, BorderLayout.NORTH);

        // Tabel transaksi
        String[] kolom = {
            "ID",
            "No. Antrean",
            "Kendaraan",
            "Paket Cuci",
            "Washer",
            "Waktu Masuk",
            "Estimasi Selesai",
            "Total Harga",
            "Status"
        };

        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(modelTabel);
        tabel.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tabel);

        add(scrollPane, BorderLayout.CENTER);

        // Panel input transaksi
        JPanel panelInput = new JPanel(new GridLayout(2, 3, 10, 10));

        panelInput.setBorder(
                BorderFactory.createTitledBorder("Input Transaksi")
        );

        cmbKendaraan = new JComboBox<>();
        cmbPaket = new JComboBox<>();
        cmbWasher = new JComboBox<>();

        panelInput.add(new JLabel("Pilih Kendaraan"));
        panelInput.add(new JLabel("Pilih Paket Cuci"));
        panelInput.add(new JLabel("Pilih Washer"));

        panelInput.add(cmbKendaraan);
        panelInput.add(cmbPaket);
        panelInput.add(cmbWasher);

        // Panel tombol
        JPanel panelTombol = new JPanel(new FlowLayout());

        btnTambah = new JButton("Tambah Antrean");
        btnProses = new JButton("Mulai Proses");
        btnSelesai = new JButton("Selesaikan");
        btnBatal = new JButton("Batalkan");
        btnStruk = new JButton("Cetak Struk");
        btnRefresh = new JButton("Refresh");
        btnKembali = new JButton("Kembali");

        panelTombol.add(btnTambah);
        panelTombol.add(btnProses);
        panelTombol.add(btnSelesai);
        panelTombol.add(btnBatal);
        panelTombol.add(btnStruk);
        panelTombol.add(btnRefresh);
        panelTombol.add(btnKembali);

        // Panel bawah
        JPanel panelBawah = new JPanel(new BorderLayout(5, 5));

        panelBawah.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10)
        );

        panelBawah.add(panelInput, BorderLayout.NORTH);
        panelBawah.add(panelTombol, BorderLayout.CENTER);

        add(panelBawah, BorderLayout.SOUTH);

        // Event tombol
        btnTambah.addActionListener(e -> tambahAntrean());
        btnProses.addActionListener(e -> mulaiProses());
        btnSelesai.addActionListener(e -> selesaikanTransaksi());
        btnBatal.addActionListener(e -> batalkanTransaksi());
        btnStruk.addActionListener(e -> cetakStruk());
        btnRefresh.addActionListener(e -> muatData());
        btnKembali.addActionListener(e -> kembaliKeMenu());

        // Memuat data pertama kali
        muatData();
    }

    // Memuat seluruh data dari database
    private void muatData() {

        try {

            // Mengambil data pelanggan
            java.util.List<Pelanggan> pelanggan =
                    pelangganDAO.semua();

            Map<Integer, Pelanggan> pelangganById = new HashMap<>();

            for (Pelanggan p : pelanggan) {
                pelangganById.put(p.getId(), p);
            }

            // Mengambil data kendaraan
            java.util.List<Kendaraan> kendaraan =
                    kendaraanDAO.semua(pelangganById);

            kendaraanById.clear();

            for (Kendaraan k : kendaraan) {
                kendaraanById.put(k.getId(), k);
            }

            // Mengambil data paket
            java.util.List<PaketCuci> paket =
                    paketDAO.semua();

            paketById.clear();

            for (PaketCuci p : paket) {
                paketById.put(p.getId(), p);
            }

            // Mengambil data washer
            daftarWasher = washerDAO.semua();

            washerById.clear();

            for (Washer w : daftarWasher) {
                washerById.put(w.getId(), w);
            }

            // Mengambil data transaksi
            daftarTransaksi = transaksiDAO.semua(
                    kendaraanById,
                    paketById,
                    washerById
            );

            tampilkanTabel();
            isiComboBox();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat data: " + e.getMessage(),
                    "Kesalahan Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Menampilkan data ke tabel
    private void tampilkanTabel() {

        modelTabel.setRowCount(0);

        for (Transaksi t : daftarTransaksi) {

            String namaWasher = "-";

            if (t.getWasher() != null) {
                namaWasher = t.getWasher().getNama();
            }

            modelTabel.addRow(new Object[]{
                t.getId(),
                t.getNoAntrean(),
                t.getKendaraan().getPlatNomor(),
                t.getPaket().getNama(),
                namaWasher,
                t.getWaktuMasuk().format(formatWaktu),
                t.getEstimasiSelesai() == null
                        ? "-"
                        : t.getEstimasiSelesai().format(formatWaktu),
                String.format("Rp %,.0f", t.getTotalHarga()),
                t.getStatus()
            });
        }
    }

    // Mengisi pilihan kendaraan, paket, dan washer
    private void isiComboBox() {

        cmbKendaraan.removeAllItems();
        cmbPaket.removeAllItems();
        cmbWasher.removeAllItems();

        for (Kendaraan k : kendaraanById.values()) {
            cmbKendaraan.addItem(k);
        }

        for (PaketCuci p : paketById.values()) {
            cmbPaket.addItem(p);
        }

        for (Washer w : daftarWasher) {

            if (w.getStatus() == StatusWasher.TERSEDIA) {
                cmbWasher.addItem(w);
            }
        }
    }

    // Membuat antrean baru
    private void tambahAntrean() {

        Kendaraan kendaraan =
                (Kendaraan) cmbKendaraan.getSelectedItem();

        PaketCuci paket =
                (PaketCuci) cmbPaket.getSelectedItem();

        if (kendaraan == null || paket == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pilih kendaraan dan paket cuci terlebih dahulu."
            );

            return;
        }

        try {

            String nomorAntrean = buatNomorAntrean();

            Transaksi transaksi = Transaksi.baru(
                    nomorAntrean,
                    kendaraan,
                    paket
            );

            transaksiDAO.tambah(transaksi);

            JOptionPane.showMessageDialog(
                    this,
                    "Antrean berhasil ditambahkan.\n"
                    + "Nomor Antrean: " + nomorAntrean
            );

            muatData();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menambahkan antrean: " + e.getMessage()
            );
        }
    }

    // Membuat nomor antrean otomatis
    private String buatNomorAntrean() {

        int nomor = 1;

        for (Transaksi t : daftarTransaksi) {

            String no = t.getNoAntrean();

            if (no != null && no.matches("A\\d+")) {

                int angka = Integer.parseInt(no.substring(1));

                if (angka >= nomor) {
                    nomor = angka + 1;
                }
            }
        }

        return String.format("A%03d", nomor);
    }

    // Memulai proses pencucian
    private void mulaiProses() {

        Transaksi transaksi = transaksiTerpilih();

        if (transaksi == null) {
            return;
        }

        Washer washer =
                (Washer) cmbWasher.getSelectedItem();

        if (washer == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tidak ada washer yang tersedia."
            );

            return;
        }

        try {

            transaksi.mulaiProses(washer);

            transaksiDAO.perbarui(transaksi);

            washerDAO.ubahStatus(
                    washer.getId(),
                    StatusWasher.BEKERJA
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Transaksi berhasil dimulai."
            );

            muatData();

        } catch (DataTidakValidException | SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memulai transaksi: " + e.getMessage()
            );
        }
    }

    // Menyelesaikan transaksi
    private void selesaikanTransaksi() {

        Transaksi transaksi = transaksiTerpilih();

        if (transaksi == null) {
            return;
        }

        try {

            Washer washer = transaksi.getWasher();

            transaksi.selesaikan();

            transaksiDAO.perbarui(transaksi);

            if (washer != null) {

                washerDAO.ubahStatus(
                        washer.getId(),
                        StatusWasher.TERSEDIA
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Transaksi berhasil diselesaikan."
            );

            muatData();

        } catch (DataTidakValidException | SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyelesaikan transaksi: " + e.getMessage()
            );
        }
    }

    // Membatalkan transaksi
    private void batalkanTransaksi() {

        Transaksi transaksi = transaksiTerpilih();

        if (transaksi == null) {
            return;
        }

        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Apakah kamu yakin ingin membatalkan transaksi ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            Washer washer = transaksi.getWasher();

            transaksi.batalkan();

            transaksiDAO.perbarui(transaksi);

            if (washer != null) {

                washerDAO.ubahStatus(
                        washer.getId(),
                        StatusWasher.TERSEDIA
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Transaksi berhasil dibatalkan."
            );

            muatData();

        } catch (DataTidakValidException | SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal membatalkan transaksi: " + e.getMessage()
            );
        }
    }

    // Mencetak struk transaksi
    private void cetakStruk() {

        Transaksi transaksi = transaksiTerpilih();

        if (transaksi == null) {
            return;
        }

        JTextArea areaStruk = new JTextArea(
                transaksi.cetakStruk()
        );

        areaStruk.setEditable(false);
        areaStruk.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(areaStruk),
                "Struk Transaksi",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Mengambil transaksi berdasarkan baris tabel yang dipilih
    private Transaksi transaksiTerpilih() {

        int baris = tabel.getSelectedRow();

        if (baris == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pilih transaksi pada tabel terlebih dahulu."
            );

            return null;
        }

        int id = (int) modelTabel.getValueAt(baris, 0);

        for (Transaksi t : daftarTransaksi) {

            if (t.getId() == id) {
                return t;
            }
        }

        return null;
    }

    // Kembali ke menu utama
    private void kembaliKeMenu() {

        new MenuUtama().setVisible(true);

        dispose();
    }
}
