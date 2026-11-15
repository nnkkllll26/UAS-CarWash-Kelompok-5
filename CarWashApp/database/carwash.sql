-- =====================================================
-- Database CarWashApp (UAS PBO SYS61516)
-- Disusun mengikuti query pada kelas DAO tim:
-- pelanggan, kendaraan, paket_cuci, washer, transaksi
-- =====================================================

CREATE DATABASE IF NOT EXISTS carwash_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE carwash_db;

DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS washer;
DROP TABLE IF EXISTS paket_cuci;
DROP TABLE IF EXISTS kendaraan;
DROP TABLE IF EXISTS pelanggan;
DROP TABLE IF EXISTS layanan; -- sisa dari versi skrip sebelumnya

CREATE TABLE pelanggan (
  id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
  nama         VARCHAR(100) NOT NULL,
  no_hp        VARCHAR(20)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE kendaraan (
  id_kendaraan INT AUTO_INCREMENT PRIMARY KEY,
  plat_nomor   VARCHAR(15) NOT NULL UNIQUE,
  merk         VARCHAR(50) NOT NULL,
  jenis        VARCHAR(10) NOT NULL,   -- nama enum JenisKendaraan: MOTOR, MOBIL, SUV
  id_pelanggan INT NOT NULL,
  CONSTRAINT fk_kendaraan_pelanggan FOREIGN KEY (id_pelanggan)
    REFERENCES pelanggan (id_pelanggan)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE paket_cuci (
  id_paket     INT AUTO_INCREMENT PRIMARY KEY,
  kode_paket   VARCHAR(20)  NOT NULL UNIQUE,
  nama_paket   VARCHAR(100) NOT NULL,
  tipe_paket   VARCHAR(20)  NOT NULL,  -- REGULER, HYDRAULIC, atau NANO
  harga_dasar  DECIMAL(12,2) NOT NULL,
  durasi_dasar INT NOT NULL,           -- dalam menit
  material     VARCHAR(100) NULL
) ENGINE=InnoDB;

-- Kolom spesialisasi dan status memakai VARCHAR karena isi enum
-- Spesialisasi dan StatusWasher mengikuti kelas model buatan anggota tim
CREATE TABLE washer (
  id_washer    INT AUTO_INCREMENT PRIMARY KEY,
  nama         VARCHAR(100) NOT NULL,
  no_hp        VARCHAR(20)  NULL,
  spesialisasi VARCHAR(30)  NOT NULL,
  status       VARCHAR(30)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE transaksi (
  id_transaksi     INT AUTO_INCREMENT PRIMARY KEY,
  no_antrean       VARCHAR(20) NOT NULL,
  id_kendaraan     INT NOT NULL,
  id_paket         INT NOT NULL,
  id_washer        INT NULL,
  status           VARCHAR(30) NOT NULL,  -- nama enum StatusAntrean
  waktu_masuk      DATETIME NOT NULL,
  estimasi_selesai DATETIME NULL,
  waktu_selesai    DATETIME NULL,
  total_harga      DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_transaksi_kendaraan FOREIGN KEY (id_kendaraan)
    REFERENCES kendaraan (id_kendaraan) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_transaksi_paket FOREIGN KEY (id_paket)
    REFERENCES paket_cuci (id_paket) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_transaksi_washer FOREIGN KEY (id_washer)
    REFERENCES washer (id_washer) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------- Data contoh -------------------
INSERT INTO pelanggan (nama, no_hp) VALUES
('Budi Santoso', '081234567801'),
('Siti Rahmawati', '081234567802'),
('Andi Pratama', '081234567803');

INSERT INTO kendaraan (plat_nomor, merk, jenis, id_pelanggan) VALUES
('T 1234 AB', 'Toyota Avanza', 'MOBIL', 1),
('T 5678 CD', 'Honda Beat', 'MOTOR', 1),
('B 9012 EF', 'Honda CR-V', 'SUV', 2),
('T 3456 GH', 'Yamaha NMAX', 'MOTOR', 3);

INSERT INTO paket_cuci (kode_paket, nama_paket, tipe_paket, harga_dasar, durasi_dasar, material) VALUES
('PKT-REG',  'Cuci Reguler',   'REGULER',   50000,  30, 'Sabun dan lap mikrofiber'),
('PKT-HYD',  'Cuci Hydraulic', 'HYDRAULIC', 75000,  45, 'Hydraulic lift dan sabun salju'),
('PKT-NANO', 'Nano Coating',   'NANO',      300000, 120, 'Cairan nano ceramic');

-- Data washer dan transaksi sengaja belum diisi.
-- Isi setelah nilai enum Spesialisasi, StatusWasher, dan StatusAntrean
-- dari anggota tim terkonfirmasi, contohnya:
-- INSERT INTO washer (nama, no_hp, spesialisasi, status) VALUES ('Nama', '08xxxxxxxxxx', 'NILAI_ENUM', 'NILAI_ENUM');
