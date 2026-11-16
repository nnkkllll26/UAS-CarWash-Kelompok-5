package carwash.view;

import carwash.config.DatabaseConnection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * Frame menu utama. Menjadi pintu masuk ke seluruh fitur aplikasi.
 * Perpindahan frame memakai pola new Frame().setVisible(true) lalu dispose().
 */
public class MenuUtama extends JFrame implements ActionListener {
    private JButton btnPelanggan, btnKendaraan, btnPaket, btnTransaksi, btnKeluar;
    private JLabel lblStatusDB;

    public MenuUtama() {
        setTitle("CarWash Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 430);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Menutup koneksi database saat jendela ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.tutup();
            }
        });

        // Bagian judul
        JLabel lblJudul = new JLabel("CarWash dan Auto Detailing", SwingConstants.CENTER);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel lblSub = new JLabel("Menu Utama", SwingConstants.CENTER);
        JPanel panelJudul = new JPanel(new GridLayout(2, 1));
        panelJudul.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));
        panelJudul.add(lblJudul);
        panelJudul.add(lblSub);

        // Bagian tombol menu
        btnPelanggan = new JButton("Data Pelanggan");
        btnKendaraan = new JButton("Data Kendaraan");
        btnPaket = new JButton("Paket Cuci");
        btnTransaksi = new JButton("Antrean dan Transaksi");
        btnKeluar = new JButton("Keluar");

        JPanel panelMenu = new JPanel(new GridLayout(5, 1, 0, 10));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        JButton[] semuaTombol = {btnPelanggan, btnKendaraan, btnPaket, btnTransaksi, btnKeluar};
        for (JButton tombol : semuaTombol) {
            tombol.addActionListener(this);
            panelMenu.add(tombol);
        }

        // Bagian status koneksi
        lblStatusDB = new JLabel(" ", SwingConstants.CENTER);
        lblStatusDB.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        add(panelJudul, BorderLayout.NORTH);
        add(panelMenu, BorderLayout.CENTER);
        add(lblStatusDB, BorderLayout.SOUTH);

        cekKoneksi();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object sumber = e.getSource();

        if (sumber == btnKendaraan) {
            new KendaraanFrame().setVisible(true);
            dispose();
        } else if (sumber == btnPelanggan) {
            // TODO anggota 2: ganti dengan new PelangganFrame().setVisible(true); dispose();
            belumTersedia("Data Pelanggan");
        } else if (sumber == btnPaket) {
            // TODO anggota 2: ganti dengan new PaketFrame().setVisible(true); dispose();
            belumTersedia("Paket Cuci");
        } else if (sumber == btnTransaksi) {
            new TransaksiFrame().setVisible(true);
            dispose();
        } else if (sumber == btnKeluar) {
            int pilihan = JOptionPane.showConfirmDialog(this,
                    "Keluar dari aplikasi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION) {
                DatabaseConnection.tutup();
                System.exit(0);
            }
        }
    }

    // Memeriksa apakah database dapat dihubungi dan menampilkan statusnya
    private void cekKoneksi() {
        try {
            DatabaseConnection.getConnection();
            lblStatusDB.setText("Database: terhubung");
            lblStatusDB.setForeground(new Color(0, 128, 0));
        } catch (SQLException ex) {
            lblStatusDB.setText("Database: tidak terhubung");
            lblStatusDB.setForeground(Color.RED);
        }
    }

    private void belumTersedia(String namaFitur) {
        JOptionPane.showMessageDialog(this,
                "Fitur " + namaFitur + " masih dalam pengerjaan.",
                "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Memakai tampilan bawaan Swing jika gagal
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MenuUtama().setVisible(true);
            }
        });
    }
}
