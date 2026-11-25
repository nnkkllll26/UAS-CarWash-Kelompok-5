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
import carwash.util.StrukPdf;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

        // Kolom ID ditampilkan di tengah, baik judul kolom maupun isinya
        DefaultTableCellRenderer rataTengah = new DefaultTableCellRenderer();
        rataTengah.setHorizontalAlignment(SwingConstants.CENTER);
        tabel.getColumnModel().getColumn(0).setCellRenderer(rataTengah);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(50);

        TableCellRenderer headerAsli = tabel.getTableHeader().getDefaultRenderer();
        tabel.getColumnModel().getColumn(0).setHeaderRenderer(
                (tbl, nilai, dipilih, fokus, noBaris, noKolom) -> {
                    Component komponen = headerAsli.getTableCellRendererComponent(
                            tbl, nilai, dipilih, fokus, noBaris, noKolom);
                    if (komponen instanceof JLabel) {
                        ((JLabel) komponen).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    return komponen;
                }
        );

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

            // Nomor antrean diambil dari database (A001, A002, ...) dan tidak boleh kembar.
            // Jika dua transaksi masuk bersamaan dan nomornya bentrok, nomor dihitung ulang.
            String nomorAntrean = null;

            for (int percobaan = 1; nomorAntrean == null; percobaan++) {

                String kandidat = transaksiDAO.nomorAntreanBerikut();

                try {

                    transaksiDAO.tambah(
                            Transaksi.baru(kandidat, kendaraan, paket)
                    );

                    nomorAntrean = kandidat;

                } catch (SQLIntegrityConstraintViolationException e) {

                    if (percobaan >= 3) {
                        throw e;
                    }
                }
            }

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

    // Mencetak struk transaksi menjadi file PDF
    private void cetakStruk() {

        Transaksi transaksi = transaksiTerpilih();

        if (transaksi == null) {
            return;
        }

        JFileChooser pemilih = new JFileChooser();

        pemilih.setDialogTitle("Simpan Struk sebagai PDF");
        pemilih.setFileFilter(
                new FileNameExtensionFilter("Dokumen PDF (*.pdf)", "pdf")
        );
        pemilih.setSelectedFile(
                new File("Struk-" + transaksi.getNoAntrean() + ".pdf")
        );

        if (pemilih.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File tujuan = pemilih.getSelectedFile();

        // Menambahkan ekstensi .pdf jika belum ada
        if (!tujuan.getName().toLowerCase().endsWith(".pdf")) {
            tujuan = new File(tujuan.getParentFile(), tujuan.getName() + ".pdf");
        }

        if (tujuan.exists()) {

            int timpa = JOptionPane.showConfirmDialog(
                    this,
                    "File " + tujuan.getName() + " sudah ada. Timpa file tersebut?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION
            );

            if (timpa != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {

            StrukPdf.simpan(transaksi, tujuan);

            int buka = JOptionPane.showConfirmDialog(
                    this,
                    "Struk berhasil disimpan:\n" + tujuan.getAbsolutePath()
                    + "\n\nBuka file sekarang?",
                    "Struk Tersimpan",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (buka == JOptionPane.YES_OPTION) {
                bukaFile(tujuan);
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan struk: " + e.getMessage(),
                    "Kesalahan",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Membuka file dengan aplikasi bawaan sistem (misalnya pembaca PDF)
    private void bukaFile(File file) {

        try {

            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {

                Desktop.getDesktop().open(file);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Sistem tidak mendukung pembukaan file otomatis.\n"
                        + "Buka manual di: " + file.getAbsolutePath()
                );
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal membuka file: " + e.getMessage()
            );
        }
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
