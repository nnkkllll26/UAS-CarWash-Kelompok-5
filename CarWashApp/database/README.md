# Database

- `carwash.sql` membuat database `carwash_db` dengan 5 tabel (pelanggan, kendaraan, paket_cuci, washer, transaksi) beserta data contoh.
  Skrip memakai `DROP TABLE IF EXISTS`, jadi data lama akan terhapus saat diimpor ulang.

Cara import (XAMPP): buka phpMyAdmin, pilih tab Import, pilih file `carwash.sql`, lalu klik Go.
Cara import (terminal): `mysql -u root -p < carwash.sql`

## Relasi Tabel

```
pelanggan (1) --- (N) kendaraan (1) --- (N) transaksi (N) --- (1) paket_cuci
                                              |
                                              (N) --- (0..1) washer
```

| Tabel | Kolom |
|---|---|
| pelanggan | id_pelanggan (PK), nama, no_hp |
| kendaraan | id_kendaraan (PK), plat_nomor (unik), merk, jenis, id_pelanggan (FK) |
| paket_cuci | id_paket (PK), kode_paket (unik), nama_paket, tipe_paket, harga_dasar, durasi_dasar, material |
| washer | id_washer (PK), nama, no_hp, spesialisasi, status |
| transaksi | id_transaksi (PK), no_antrean, id_kendaraan (FK), id_paket (FK), id_washer (FK, boleh kosong), status, waktu_masuk, estimasi_selesai, waktu_selesai, total_harga |

## Nilai Enum yang Disimpan sebagai Teks

| Kolom | Nilai |
|---|---|
| kendaraan.jenis | MOTOR, MOBIL, SUV |
| paket_cuci.tipe_paket | REGULER, HYDRAULIC, NANO |
| washer.spesialisasi | UMUM, HYDRAULIC, DETAILING |
| washer.status | TERSEDIA, BEKERJA, LIBUR |
| transaksi.status | MENUNGGU, DIPROSES, SELESAI, DIBATALKAN |
