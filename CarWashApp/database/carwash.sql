-- =====================================================
-- Database CarWashApp (UAS PBO SYS61516)
-- Disusun mengikuti query pada kelas DAO tim:
-- pelanggan, kendaraan, paket_cuci, washer, transaksi
-- PERINGATAN: skrip ini menghapus lalu membuat ulang semua tabel,
-- jadi data lama di carwash_db akan hilang setiap kali diimpor.
-- =====================================================

CREATE DATABASE IF NOT EXISTS carwash_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE carwash_db;

DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS washer;
DROP TABLE IF EXISTS paket_cuci;
DROP TABLE IF EXISTS kendaraan;
DROP TABLE IF EXISTS pelanggan;

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

-- Kolom spesialisasi dan status menyimpan nama enum Java:
-- spesialisasi: UMUM, HYDRAULIC, DETAILING
-- status      : TERSEDIA, BEKERJA, LIBUR
CREATE TABLE washer (
  id_washer    INT AUTO_INCREMENT PRIMARY KEY,
  nama         VARCHAR(100) NOT NULL,
  no_hp        VARCHAR(20)  NULL,
  spesialisasi VARCHAR(30)  NOT NULL,
  status       VARCHAR(30)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE transaksi (
  id_transaksi     INT AUTO_INCREMENT PRIMARY KEY,
  no_antrean       VARCHAR(20) NOT NULL UNIQUE,
  id_kendaraan     INT NOT NULL,
  id_paket         INT NOT NULL,
  id_washer        INT NULL,
  status           VARCHAR(30) NOT NULL,  -- MENUNGGU, DIPROSES, SELESAI, DIBATALKAN
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

INSERT INTO washer (nama, no_hp, spesialisasi, status) VALUES
('Rudi Hartono',  '081200000001', 'UMUM',      'TERSEDIA'),
('Dimas Saputra', '081200000002', 'HYDRAULIC', 'BEKERJA'),
('Eko Prasetyo',  NULL,           'DETAILING', 'LIBUR');

-- Total harga mengikuti rumus model: harga dasar x faktor jenis (+ biaya hydraulic Rp 10.000)
-- Avanza (MOBIL) + Reguler        = 50.000 x 1,0          = 50.000, 30 menit
-- Honda Beat (MOTOR) + Hydraulic  = 75.000 x 0,5 + 10.000 = 47.500, 23 menit
-- CR-V (SUV) + Nano Coating       = 300.000 x 1,3         = 390.000, 150 menit
INSERT INTO transaksi (no_antrean, id_kendaraan, id_paket, id_washer, status,
                       waktu_masuk, estimasi_selesai, waktu_selesai, total_harga) VALUES
('CW-20261115-001', 1, 1, 1,    'SELESAI',  '2026-11-15 08:00:00', '2026-11-15 08:30:00', '2026-11-15 08:28:00', 50000),
('CW-20261115-002', 2, 2, 2,    'DIPROSES', '2026-11-15 09:00:00', '2026-11-15 09:23:00', NULL,                  47500),
('CW-20261115-003', 3, 3, NULL, 'MENUNGGU', '2026-11-15 09:10:00', '2026-11-15 11:40:00', NULL,                  390000);
